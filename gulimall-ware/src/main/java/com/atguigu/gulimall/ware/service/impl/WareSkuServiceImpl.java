package com.atguigu.gulimall.ware.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atguigu.common.to.mq.OrderEntityTo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.exception.NoStockException;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.mq.StockDetailTo;
import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.atguigu.gulimall.ware.entity.WareOrderTaskEntity;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.feign.OrderFeignService;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.WareOrderTaskDetailService;
import com.atguigu.gulimall.ware.service.WareOrderTaskService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.OrderEntityVo;
import com.atguigu.gulimall.ware.vo.OrderItemVo;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.Data;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    WareOrderTaskService orderTaskService;
    @Autowired
    WareOrderTaskDetailService orderTaskDetailService;
    @Autowired
    OrderFeignService orderFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id", skuId);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1. 检查当前库存，如果有库存，则更新，无库存则新增
        List<WareSkuEntity> wareSkuEntities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if(wareSkuEntities == null || wareSkuEntities.size() == 0){//新增
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            // 【已完成】 远程查询SKU名字。try表示发生异常，事务不需要回滚（处理的方式一）。其他方式见高级
            try {
                R info = productFeignService.info(skuId);
                if(info.getCode() == 0){
                    Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                    wareSkuEntity.setSkuName((String) data.get(("skuName")));
                }
            }catch (Exception e){

            }
            this.baseMapper.insert(wareSkuEntity);
        }else {
            this.baseMapper.addStock(skuId, wareId, skuNum);//this.baseMapper即WareSkuDao
        }
    }

    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id = 1
            Long count = this.baseMapper.getSkuStock(skuId);
            vo.setSkuId(skuId);
            vo.setHasStock(count==null ? false : count > 0);
            return vo;
        }).collect(Collectors.toList());
        return collect;
    }

    @Transactional(rollbackFor = NoStockException.class)//可以不标NoStockException，因为它继承于runtimeexception，只要是exception，默认都会回滚
    @Override
    public Boolean orderLockStock(WareSkuLockVo vo) {
        // 0. 保存库存工作单信息，用于rabbit追溯（需要放到锁库存之前，不然先锁了库存，但没有成功创建库存工作单（如锁定库存是个远程服务），会导致解锁不到刚才锁定的库存）
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSN());
        taskEntity.setCreateTime(new Date());
        orderTaskService.save(taskEntity);

        // 1. 按照下单的收获地址，找到一个就近的仓库锁定库存
        // 1.1 找到每个商品在哪个仓库有库存
        List<OrderItemVo> locks = vo.getLocks();//得到每个商品项
        List<SkuWareHasStock> collect = locks.stream().map(item -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            // 1.1.1 查询当前sku在哪个仓库有库存
            List<Long> wareIds = this.baseMapper.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIds);
            return stock;
        }).collect(Collectors.toList());

        // 2. 锁定库存
        for(SkuWareHasStock hasStock : collect){
            Boolean skuStocked = false;//当前商品库存锁定标志位
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if(wareIds == null || wareIds.size() == 0){//所有仓库都没这个商品
                throw new NoStockException(skuId);
            }
            // 遍历每一个仓库
            for(Long wareId : wareIds){
                Long count = this.baseMapper.lockSkuStock(skuId, wareId, hasStock.getNum());//返回影响行数
                if(count == 1){
                    skuStocked = true;
                    // 告诉rabbitmq库存锁定成功（对于锁定失败的，抛出异常，回滚锁定库存事务（本地事务），所以id会不存在，rabbitmq查不到，也就不用去管）
                    //
                    WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(null,skuId,null,hasStock.getNum(),taskEntity.getId(),wareId,1);
                    orderTaskDetailService.save(detailEntity);
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo stockDetailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity,stockDetailTo);
                    lockedTo.setDetailTo(stockDetailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);

                    break;//
                }else{//当前仓库锁定失败，重试下一个仓库


                }
            }
            if(!skuStocked){//所有仓库都遍历完了但是还没锁住库存
                throw new NoStockException(skuId);
            }
        }

        // 3. 能到这里肯定都是全部锁定成功了
        return true;
    }

    @Override
    public void unLockStock(StockLockedTo to) {
        // 1. 获取锁定的订单项信息
        StockDetailTo detailTo = to.getDetailTo();
        Long detailId = detailTo.getId();

        // 2. 解锁锁定库存
        // 2.1 查询数据库`wms_ware_order_task`关于这个订单的锁定库存信息（如果查不到，表示锁定库存时失败，无需回滚）(如果能查到，说明库存锁定成功，需要判断订单情况）
        WareOrderTaskDetailEntity byId = orderTaskDetailService.getById(detailId);
        if (byId != null) {//库存锁定成功
            // 2.1.1 是否有这个库存对应的订单（如果没有，必须解锁，如果有，则判断是否支付）
            Long id = to.getId();//库存工作单的id（`wms_ware_order_task`）
            WareOrderTaskEntity taskEntity = orderTaskService.getById(id);
            String orderSn = taskEntity.getOrderSn();//锁定库存时的订单号
            R r = orderFeignService.getOrderStatus(orderSn);
            if (r.getCode() == 0) {//远程查询成功
                OrderEntityVo data = r.getData(new TypeReference<OrderEntityVo>() {
                });
                if (data == null || data.getStatus() == 4) {//订单已被取消（解锁库存）或者//订单不存在
                    if(byId.getLockStatus() == 1){//当前工作单状态1表示已锁定但未解锁，才能解锁
                        unLockStock(detailTo.getSkuId(), detailTo.getWareId(), detailTo.getSkuNum(), detailId);
                    }
                }
            }else {
                throw new RuntimeException("远程服务失败");
            }
        }
    }

    @Transactional
    @Override
    public void unLockStock(OrderEntityTo to) {
        String orderSn = to.getOrderSn();
//        R r = orderFeignService.getOrderStatus(orderSn);//其实没必要再去查，to已经时最新状态了
        WareOrderTaskEntity taskEntity = orderTaskService.getOrderTaskByOrderSn(orderSn);//查一下最新库存状态，防止重复解锁库存
        Long id = taskEntity.getId();
        // 按照工作单找到所有没有解锁的库存，进行解锁
        List<WareOrderTaskDetailEntity> list = orderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", id).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity entity : list) {
            this.unLockStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum(),entity.getId());
        }
    }

    /**
     * 解锁库存
     * @param skuId
     * @param wareId
     * @param num
     * @param detailId
     */
    private void unLockStock(Long skuId, Long wareId, Integer num,Long detailId){
        this.baseMapper.unlockStock(skuId,wareId,num);//解锁库存
        // 更新库存工作单状态
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(detailId);
        wareOrderTaskDetailEntity.setLockStatus(2);//2表示已解锁
        orderTaskDetailService.updateById(wareOrderTaskDetailEntity);

    }

    /**
     * 只在本类中使用，所以可以设置为内部类
     */
    @Data
    class SkuWareHasStock{
        private Long skuId;
        private List<Long> wareId;
        private Integer num;
    }

}