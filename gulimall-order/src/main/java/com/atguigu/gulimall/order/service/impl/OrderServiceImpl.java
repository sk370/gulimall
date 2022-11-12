package com.atguigu.gulimall.order.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.SecKillOrderTo;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.mq.OrderEntityTo;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.constant.OrderConstant;
import com.atguigu.gulimall.order.constant.OrderStatusEnum;
import com.atguigu.gulimall.order.dao.OrderItemDao;
import com.atguigu.gulimall.order.entity.OrderItemEntity;
import com.atguigu.gulimall.order.entity.PaymentInfoEntity;
import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.MemberFeignService;
import com.atguigu.gulimall.order.feign.ProductFeignService;
import com.atguigu.gulimall.order.feign.WmsFeignService;
import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.service.OrderItemService;
import com.atguigu.gulimall.order.service.PaymentInfoService;
import com.atguigu.gulimall.order.to.OrderCreateTo;
import com.atguigu.gulimall.order.vo.*;
import com.atguigu.gulimall.order.vo.pay.PayAsyncVo;
import com.atguigu.gulimall.order.vo.pay.PayVo;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.aspectj.weaver.ast.Or;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();//利用threadlocal传递参数
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    WmsFeignService wmsFeignService;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    PaymentInfoService paymentInfoService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();//获取当前登录的用户信息
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();//即threadlocal

        // 1. 查询会员的收获地址
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);//保持旧请求的上下文信息
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddresses(address);
        }, executor);

        // 2. 查询购物车中的购物项
        CompletableFuture<Void> getCartItemFuture = CompletableFuture.runAsync(() -> {
            // feign远程调用会创建新请求，新请求没有请求头信息，需要在feign拦截器中配置
            RequestContextHolder.setRequestAttributes(requestAttributes);//保持旧请求的上下文信息
            List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
            confirmVo.setOrderItems(currentUserCartItems);
        }, executor).thenRunAsync(()->{// 4. 查询库存信息
            RequestContextHolder.setRequestAttributes(requestAttributes);//保持旧请求的上下文信息
            List<OrderItemVo> orderItems = confirmVo.getOrderItems();
            List<Long> collect = orderItems.stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            R skuHasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuHasStockVo> data = skuHasStock.getData("data", new TypeReference<List<SkuHasStockVo>>() {
            });
            if(data != null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
                confirmVo.setStocks(map);
            }
        },executor);

        // 3. 查询用户的积分
        CompletableFuture<Void> getIntegration = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);//保持旧请求的上下文信息
            Integer integration = memberRespVo.getIntegration();
            confirmVo.setIntegration(integration);
        }, executor);

        // 4. 其他数据（付款金额）自动计算，不写了

        // 5. 防重令牌，接口幂等性
        String token = UUID.randomUUID().toString().replace("-", "");//生成防重令牌
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFX + memberRespVo.getId(), token,30, TimeUnit.MINUTES);//给服务器一个
        confirmVo.setOrderToken(token);//给前端一个
        CompletableFuture.allOf(getAddressFuture,getCartItemFuture,getIntegration).get();
        return confirmVo;
    }

//    @GlobalTransactional//seata的全局事务(高并发性能低，使用rabbitmq解决，不用这个）
    @Transactional//本地事务，只能控制自己的回滚，控制不了其他服务的回滚
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        responseVo.setCode(0);//默认设置为0，表示成功，修改为其他数值时表示失败
        confirmVoThreadLocal.set(vo);//设置要共享的数据（用于参数传递）

        // 点击下单：完成订单创建、验证令牌、价格确认、锁定库存等一系列操作。
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        // 1. 验证令牌[令牌的对比和删除必须保证原子性]
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";//获取指定值，不存在返回0，删除失败返回0，删除成功1
        String orderToken = vo.getOrderToken();//前端提交的令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFX + memberRespVo.getId()),
                orderToken);//该语句等价于下面的if判断，但是if判断不保证原子性
//        String serverToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFX + memberRespVo.getId());//服务器中的令牌
//        if (serverToken != null && Objects.equals(orderToken, serverToken)) {//验证令牌
//            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFX + memberRespVo.getId());
//            return responseVo;
//        }else {
//            return responseVo;
//        }
        if(result == 0){//令牌验证失败
            responseVo.setCode(1);//失败的代码为1
            return responseVo;
        }else {
            // 2. 创建订单
            OrderCreateTo order = createOrder();
            // 3. 验证价格
            BigDecimal payAmount = order.getOrder().getPayAmount();//根据订单项计算的价格
            BigDecimal payPrice = vo.getPayPrice();//页面提交的总价
            if(Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01){//二者价格差小于0.01即认为验证成功
                // 4. 保存订单到数据库
                saveOrder(order);

                // 5. 锁定库存（只要有异常就回滚数据）——订单号、所有订单项（skuid，skunum、skuname）、
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setOrderSN(order.getOrder().getOrderSn());
                List<OrderItemVo> collect = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setLocks(collect);
                R r = wmsFeignService.orderLockStock(wareSkuLockVo);
                if(r.getCode() == 0){//锁定成功
                    responseVo.setOrder(order.getOrder());
//                    int i = 10 /0;//模拟异常，测试seata分布式事务，如果库存服务正常回滚，表明测试seata分布式事务成功起作用
//                    int i = 10 /0;//模拟异常，测试rebbitmq延时队列，如果延时队列起作用，表明测试成功
                    // 订单创建成功，给rabbitmq发送消息
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order",order.getOrder());
                    return responseVo;
                } else {
                    responseVo.setCode(3);//锁定库存失败
                    Integer code = r.getCode();
                    throw new NoStockException(code.longValue());//由于事务需要发生异常才能回滚，没有这句时会下单失败，但给数据库添加了数据
                }
            } else {
                responseVo.setCode(2);//金额对比失败
                throw new RuntimeException();//同样是为了回滚事务
//                return responseVo;
            }
        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    @Override
    public void closeOrder(OrderEntity entity) {
        // 查询当前订单的最新状态，是否已经支付等
        OrderEntity byId = this.getById(entity.getId());
        if(byId.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){//待支付的订单
            // 关闭订单
            OrderEntity order = new OrderEntity();//创建一个新的订单对象，去更新byid对象，避免正在更新状态时，其他服务对byid进行了修改，然后这里又改了回去
            order.setId(entity.getId());
            order.setStatus(OrderStatusEnum.CANCLED.getCode());//取消订单
            this.updateById(order);

            // 给库存mq的队列发送一个消息
            OrderEntityTo orderEntityTo = new OrderEntityTo();
            BeanUtils.copyProperties(byId, orderEntityTo);
            try {// 保证消息一定能发送出去
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderEntityTo);
            }catch (Exception e){
                // TODO 没发送出去的消息进行循环重试、
            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));

        payVo.setBody(order.getNote());//订单的备注
        payVo.setOut_trade_no(order.getOrderSn());//订单号
        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);//格式化bigdecimal
        payVo.setTotal_amount(bigDecimal.toString());//订单金额

        payVo.setSubject(order_sn.get(0).getSkuName());//订单主题

        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberRespVo.getId()).orderByDesc("id")
        );
        //查询每个订单的订单项
        List<OrderEntity> collect = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(order_sn);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(collect);//重新设置分页信息
        return new PageUtils(page);
    }

    @Override
    public String handlePayResult(PayAsyncVo vo, HttpServletRequest request){
        // 1. 保存交易流水
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setOrderSn(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());
        infoEntity.setCreateTime(new Date());
//        infoEntity.setOrderId(vo.get);//需要查询ordersn获取
        infoEntity.setTotalAmount(new BigDecimal(vo.getBuyer_pay_amount()));

        paymentInfoService.save(infoEntity);

        // 2. 修改订单的状态信息
        if(Objects.equals(vo.getTrade_status(), "TRADE_SUCCESS") || Objects.equals(vo.getTrade_status(), "TRADE_FINISHED")){
            String orderSn = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(orderSn, OrderStatusEnum.PAYED.getCode());
            // TODO 怎么主动延时队列中的订单消息和库存锁定消息？不主动处理等着自动过期处理吗？
        }
        return "success";
    }

//    @Transactional
    @Override
    public void createSecKillOrder(SecKillOrderTo secKillOrder) {
        String orderSn = secKillOrder.getOrderSn();
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
//        if(order_sn == null) {
            // 1. TODO 保存订单信息
            OrderEntity orderEntity = new OrderEntity();
            BigDecimal multiply = secKillOrder.getSeckillPrice().multiply(new BigDecimal(secKillOrder.getNum() + ""));
            orderEntity.setPayAmount(multiply);
            orderEntity.setOrderSn(secKillOrder.getOrderSn());
            orderEntity.setMemberId(secKillOrder.getMemberId());
//        orderEntity.setReceiverRegion();
            orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
            orderEntity.setCreateTime(new Date());
            orderEntity.setModifyTime(new Date());
            this.save(orderEntity);

            // 2. TODO 保存订单项信息
            OrderItemEntity entity = new OrderItemEntity();
            entity.setOrderSn(secKillOrder.getOrderSn());
            entity.setSkuId(secKillOrder.getSkuId());
            entity.setRealAmount(multiply);
            entity.setSkuQuantity(secKillOrder.getNum());
//            R r = productFeignService.getSpuInfoBySkuId(secKillOrder.getSkuId());//调用太慢导致消息消费不了，可以写一个方法，将返回值放在springcache中
//            SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
//            });
//            entity.setSpuId(data.getId());
//            entity.setSpuBrand(data.getBrandId().toString());
//            entity.setSpuName(data.getSpuName());
//            entity.setCategoryId(data.getCatalogId());
            orderItemService.save(entity);

            // 3. TODO 锁定库存信息【也要给库存延时队列消息】
//        }
    }

    /**
     * 保存订单到数据库
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        // 1. 保存订单
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setCreateTime(new Date());
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        // 2. 保存订单项
        List<OrderItemEntity> orderItems = order.getOrderItems();
//        orderItemService.saveBatch(orderItems);//at io.seata.rm.datasource.AbstractPreparedStatementProxy.addBatch(AbstractPreparedStatementProxy.java:252)
//这是一个和mybatis-plus的addbatch方法的bug，只能将批量保存变成增强for循环单个保存
        for(OrderItemEntity item : orderItems){
            orderItemService.save(item);
        }
    }

    /**
     * 创建订单
     * @return
     */
    private OrderCreateTo createOrder(){
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1. 订单信息
        // 1.1 生成订单号
        String orderSN = IdWorker.getTimeId();//订单号，使用mybatis的工具类
        OrderEntity orderEntity = buildOrder(orderSN);
        orderCreateTo.setOrder(orderEntity);

        // 2. 获取到所有的订单项
        List<OrderItemEntity> orderItems = buildOrderItems(orderSN);
        orderCreateTo.setOrderItems(orderItems);

        // 3. 计算价格、积分
        OrderEntity order = computePrice(orderEntity, orderItems);
        orderCreateTo.setOrder(order);

        return orderCreateTo;
    }

    /**
     * 计算订单项金额的总和，作为订单的总额（最新金额）
     * @param orderEntity
     * @param orderItems
     */
    private OrderEntity computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        // 1. 订单相关的价格：订单总额、应付额度、运费
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");//优惠总金额
        BigDecimal promotion = new BigDecimal("0.0");//优惠总金额
        BigDecimal integration = new BigDecimal("0.0");//优惠总金额
        Integer giftIntegration = new Integer(0);//当前订单总积分
        Integer giftGrowth = new Integer(0);//当前订单总成长值
        for(OrderItemEntity entity : orderItems){
            BigDecimal realAmount = entity.getRealAmount();
            BigDecimal couponAmount = entity.getCouponAmount();//优惠券的金额
            BigDecimal promotionAmount = entity.getPromotionAmount();//促销的金额
            BigDecimal integrationAmount = entity.getIntegrationAmount();//积分优惠的金额
            giftIntegration += entity.getGiftIntegration();//积分信息
            giftGrowth += entity.getGiftGrowth();//成长值
            total = total.add(realAmount);
            coupon = coupon.add(couponAmount);
            promotion = promotion.add(promotionAmount);
            integration = integration.add(integrationAmount);
        }
        orderEntity.setTotalAmount(total);//这是重新根据订单项计算出来的，相当于最新的
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));//应付总额（加了运费的）

        orderEntity.setCouponAmount(coupon);
        orderEntity.setPromotionAmount(promotion);//优惠总额
        orderEntity.setIntegrationAmount(integration);

        orderEntity.setIntegration(giftIntegration);
        orderEntity.setGrowth(giftGrowth);

        return orderEntity;
    }

    /**
     * 构建订单信息
     * @param orderSN
     * @return
     */
    private OrderEntity buildOrder(String orderSN) {
        MemberRespVo respVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSN);
        orderEntity.setMemberId(respVo.getId());

        // 1. 收货地址信息及运费信息
        OrderSubmitVo submitVo = confirmVoThreadLocal.get();
        R data = wmsFeignService.getFare(submitVo.getAddrId());
        FareRespvO fareRespvO = data.getData(new TypeReference<FareRespvO>(){});
        BigDecimal fare = fareRespvO.getFare();
        orderEntity.setFreightAmount(fare);
        orderEntity.setReceiverCity(fareRespvO.getAddress().getCity());
        orderEntity.setReceiverDetailAddress((fareRespvO.getAddress().getDetailAddress()));
        orderEntity.setReceiverPhone(fareRespvO.getAddress().getPhone());
        orderEntity.setReceiverName(fareRespvO.getAddress().getName());
        orderEntity.setReceiverPostCode(fareRespvO.getAddress().getPostCode());
        orderEntity.setReceiverRegion(fareRespvO.getAddress().getRegion());
        orderEntity.setReceiverProvince(fareRespvO.getAddress().getProvince());

        // 2. 订单状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);//未确认收货，7天自动收货
        orderEntity.setDeleteStatus(0);//表示未删除

        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSN) {
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();//这是最后一次确定每个购物项目的价格
        if(currentUserCartItems != null && currentUserCartItems.size() > 0){
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItem(cartItem);
                orderItemEntity.setOrderSn(orderSN);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return itemEntities;
        }
        return null;
    }

    /**
     * 构建单个订单项数据
     * @param orderItemVo
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo orderItemVo) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 1. 订单信息：订单号(在buildOrderItems方法中设置了）

        // 2. 商品的spu信息
        R r = productFeignService.getSpuInfoBySkuId(orderItemVo.getSkuId());
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>() {
        });
        orderItemEntity.setSpuId(data.getId());
        orderItemEntity.setSpuBrand(data.getBrandId().toString());
        orderItemEntity.setSpuName(data.getSpuName());
        orderItemEntity.setCategoryId(data.getCatalogId());

        // 3. 商品的sku信息
        orderItemEntity.setSkuId(orderItemVo.getSkuId());
        orderItemEntity.setSkuName(orderItemVo.getTitle());
        orderItemEntity.setSkuPic(orderItemVo.getImage());
        orderItemEntity.setSkuPrice(orderItemVo.getPrice());
        String s = StringUtils.collectionToDelimitedString(orderItemVo.getSkuAttr(), ";");//使用工具类将数组转换为指定分隔符分割的字符串
        orderItemEntity.setSkuAttrsVals(s);
        orderItemEntity.setSkuQuantity(orderItemVo.getCount());

        // 4.商品的优惠信息（未做）

        // 5. 积分信息
        orderItemEntity.setGiftGrowth(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());//价格的整数值作为积分
        orderItemEntity.setGiftIntegration(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());//价格的整数值作为成长值

        // 6. 订单项的价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));//促销价格
        orderItemEntity.setCouponAmount(new BigDecimal("0.0"));//优惠券
        orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));//积分兑换的优惠
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));//未优惠的总价
        BigDecimal realPrice = origin.subtract(orderItemEntity.getPromotionAmount()).subtract(orderItemEntity.getCouponAmount()).subtract(orderItemEntity.getIntegrationAmount());//使用各类优惠后的价格（应付价格）
        orderItemEntity.setRealAmount(realPrice);//当前订单项的实际金额（应付金额）

        return orderItemEntity;
    }

}