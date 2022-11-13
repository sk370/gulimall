package com.atguigu.gulimall.secondkill.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.SecKillOrderTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.secondkill.feign.CouponFeignService;
import com.atguigu.gulimall.secondkill.feign.ProductFeignService;
import com.atguigu.gulimall.secondkill.inteceptor.LoginUserInterceptor;
import com.atguigu.gulimall.secondkill.service.SecKillService;
import com.atguigu.gulimall.secondkill.vo.SecKillSessionsWithSkusVo;
import com.atguigu.gulimall.secondkill.vo.SecKillSkuVo;
import com.atguigu.gulimall.secondkill.vo.SkuInfoVo;
import com.atguigu.gulimall.secondkill.vo.to.SecKillSkuRedisTo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillServiceImpl
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 15:30
 */
@Service
public class SecKillServiceImpl implements SecKillService {
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    RedissonClient redissonClient;

    private final String SESSIONS_CACHE_PREFIX="seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX="seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";

    @Override
    public void uploadSecKillSkuLatest3Days() {
        // 1. 查询需要参与秒杀的商品
        R latest3DaysSession = couponFeignService.getLatest3DaysSession();
        if(latest3DaysSession.getCode() == 0){
            // 1.1 得到商品项目
            List<SecKillSessionsWithSkusVo> sessionData = latest3DaysSession.getData(new TypeReference<List<SecKillSessionsWithSkusVo>>() {
            });
            if(sessionData == null) return;
            // 2. 上架商品（存入redis）
            // 2.1 缓存活动信息
            this.saveSessionInofs(sessionData);
            // 2.2 缓存活动关联的商品信息
            this.saveSessionSkuInfos(sessionData);
        }
    }

    @Override
    public List<SecKillSkuRedisTo> getCurrentSecKillSkus() {
        // 1. 确定当前时间的秒杀场次
        Long time = new Date().getTime();//距离1970年的差值
        Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
        for (String key : keys) {
            String timeString = key.replace(SESSIONS_CACHE_PREFIX, "");
            String[] s = timeString.split("_");
            Long start = Long.parseLong(s[0]);
            Long end = Long.parseLong(s[1]);
            if(time >= start && time <= end){
                // 2. 获取当前场次的商品信息
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);//取出当前场次中有多少商品要上架，这里取的范围是list的索引-100，到100
                BoundHashOperations<String,String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<String> list = hashOps.multiGet(range);//获取
                if(list != null && list.size() > 0){
                    List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
                        SecKillSkuRedisTo secKillSkuRedisTo = JSON.parseObject(item.toString(), SecKillSkuRedisTo.class);
//                        secKillSkuRedisTo.setRandomCode(null);//如果不是返回当前的秒杀，则千万不能返回随机码
                        return secKillSkuRedisTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
//                break;//老师的有这个，如果在当前时间内秒杀的场次多余一场呢？比如当前11点，秒杀有10点到12点，10点到14点，写了break，不久只能查到一场？
            }
        }
        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSecKillInfo(Long skuId) {
        // 1. 找到所有参与秒杀的商品，在redis中保存时的key
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = hashOps.keys();
        if(keys!= null && keys.size() > 0){
            String regx = "\\d_" + skuId;
            for (String key : keys) {
                boolean matches = Pattern.matches(regx, key);
                if(matches){
                    String json = hashOps.get((key));
                    SecKillSkuRedisTo secKillSkuRedisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
                    Long startTime = secKillSkuRedisTo.getStartTime();
                    Long endTime = secKillSkuRedisTo.getEndTime();
                    long now = new Date().getTime();
                    if(now >= startTime && now <= endTime){//当前商品正在秒杀

                    }else {//未在秒杀时间段，随机uuid不能发给前台，防止作弊
                        secKillSkuRedisTo.setRandomCode(null);
                    }
                    return secKillSkuRedisTo;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, String num) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();//获取当前用户信息

        // 1. 获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        // 1.1 获取秒杀单信息（商品信息）
        String s = hashOps.get(killId);
        if(StringUtils.isEmpty(s)){
            return null;
        }{
            SecKillSkuRedisTo secKillSkuRedisTo = JSON.parseObject(s, SecKillSkuRedisTo.class);
            // 1.2 校验合法性
            // 1.2.1 校验时间
            Long startTime = secKillSkuRedisTo.getStartTime();
            Long endTime = secKillSkuRedisTo.getEndTime();
            long time = new Date().getTime();
            if(time >= startTime && time <= endTime){
                // 1.2.2 校验随机码和商品id
                String randomCode = secKillSkuRedisTo.getRandomCode();
                String skuId = secKillSkuRedisTo.getPromotionSessionId() + "_" + secKillSkuRedisTo.getSkuId();
                if(Objects.equals(randomCode, key) && Objects.equals(killId,skuId)){
                    // 1.2.3 验证数量是否合理
                    if(new BigDecimal(num).compareTo(secKillSkuRedisTo.getSeckillLimit()) < 1){//num <= limit
                        // 1.2.4 判断当前用户是否已经买过：幂等性：如果秒杀成功，使用用户id、场次id、商品id进行占位
                        String redisKey = memberRespVo.getId() + "_" + skuId;
                        long ttl = endTime - startTime;//毫秒数
                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num, ttl, TimeUnit.MILLISECONDS);//如果买过就setnx一个数在redis，过期时间为商品秒杀时间长
                        if(aBoolean){//占位成功（存入redis中了），则表示从来每买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + key);//获取信号量
//                                boolean b = semaphore.tryAcquire(Integer.parseInt(num), 100, TimeUnit.MILLISECONDS);//取出指定数量，acquire会造成阻塞
                                boolean b = semaphore.tryAcquire(Integer.parseInt(num));//取出指定数量，没有等待时间，不会造成阻塞
                                if(b) {
                                    // 1.3 秒杀成功 快速下单（发送mq消息）
                                    String timeId = IdWorker.getTimeId();//mybatis工具，获取一个订单号
                                    SecKillOrderTo secKillOrderTo = new SecKillOrderTo();
                                    secKillOrderTo.setOrderSn(timeId);
                                    secKillOrderTo.setNum(Integer.parseInt(num));
                                    secKillOrderTo.setMemberId(memberRespVo.getId());
                                    secKillOrderTo.setSeckillPrice(secKillSkuRedisTo.getSeckillPrice());
                                    secKillOrderTo.setPromotionSessionId(secKillSkuRedisTo.getPromotionSessionId());
                                    secKillOrderTo.setSkuId(secKillSkuRedisTo.getSkuId());
                                    rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", secKillOrderTo);//发给reabbitmq，进行订单处理【应该到秒杀的延时队列里面】
                                    return timeId;
                                }else{
                                    return null;
                                }
                        }else{
                            return null;
                        }
                    }
                }else {
                    return null;
                }
            }else {
                return null;
            }
        }
        return null;
    }

    /**
     * 缓存活动信息
     * @param sessions
     */
    private void saveSessionInofs(List<SecKillSessionsWithSkusVo> sessions){
        sessions.stream().forEach(session ->{//        sessions.forEach();//有何区别
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;

            // 判断redis中是否已经加入了，加入了就不用重复加入了
            Boolean hasKey = redisTemplate.hasKey(key);
            if(!hasKey) {
                List<String> collect = session.getRelationSkus().stream().map(item -> {
                    return item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString();//场次id_商品id，作为保存值
                }).collect(Collectors.toList());
                // TODO 只添加场次，不关联商品，这里item.getPromotionSessionId().toString() + "_" + item.getSkuId().toString()为空
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
        });
    }

    /**
     * 缓存活动关联的商品信息
     * @param sessions
     */
    private void saveSessionSkuInfos(List<SecKillSessionsWithSkusVo> sessions){
        sessions.stream().forEach(session ->{//        sessions.forEach();//有何区别
            // 准备hash操作
            BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            List<SecKillSkuVo> relationSkus = session.getRelationSkus();
            relationSkus.stream().forEach(sku -> {

                Boolean key = hashOps.hasKey(sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString());// 判断当前场次的sku是否已经在redis中有
                if(!key) {
                    SecKillSkuRedisTo secKillSkuRedisTo = new SecKillSkuRedisTo();
                    secKillSkuRedisTo.setSeckillLimit(sku.getSeckillLimit());
                    // 1. sku的详情数据
                    R skuInfo = productFeignService.getSkuInfo(sku.getSkuId());
                    if (skuInfo.getCode() == 0) {
                        SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        secKillSkuRedisTo.setSkuInfo(info);
                    }
                    // 2. sku的秒杀信息(基本信息）
                    BeanUtils.copyProperties(sku, secKillSkuRedisTo);

                    // 3. 设置当前商品的秒杀时间信息
                    secKillSkuRedisTo.setStartTime(session.getStartTime().getTime());
                    secKillSkuRedisTo.setEndTime(session.getEndTime().getTime());

                    // 4. 设置商品的随机码（用户抢货时传参，也能防止内部人员枪单）:
                    String token = UUID.randomUUID().toString().replace("-", "");
                    secKillSkuRedisTo.setRandomCode(token);

                    String s = JSON.toJSONString(secKillSkuRedisTo);
                    hashOps.put(sku.getPromotionSessionId().toString() + "_" + sku.getSkuId().toString(), s);//场次id_商品id，作为保存值(键值)，单纯以sku做值（键），则不同场次的相同商品不会加入到秒杀里

                    // 5. 设置分布式信号量（数值，等于秒杀的库存）限制流量
                    // 库存的value是信号量，key是uuid
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(sku.getSeckillCount().intValue());
                }
            });
        });
    }
}
