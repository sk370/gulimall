package com.atguigu.gulimall.ware.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.atguigu.common.exception.NoStockException;
import com.atguigu.gulimall.ware.vo.OrderItemVo;
import com.atguigu.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.feign.ProductFeignService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignService productFeignService;

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