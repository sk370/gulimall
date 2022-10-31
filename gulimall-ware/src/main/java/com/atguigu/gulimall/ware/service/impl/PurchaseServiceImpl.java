package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.PurchaseService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergeVo;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVo;
import com.atguigu.gulimall.ware.vo.PurchaseItemDoneVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;
    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();//采购单id       `wms_purchase` 表
        if(purchaseId == null){
            // 新建一个采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatus.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // 合并采购单功能
        List<Long> items = mergeVo.getItems();//采购需求id `wms_purchase_detail`表

        //采购单与采购项仓库不同、采购项之间仓库不同可以合并，等到采购结束往仓库存时，去查询`wms_purchase_detail`表。
        //只是这样的话，`wms_purchase`表中的wareid就多余了

        Long finalPurchaseId = purchaseId;//map方法中的箭头函数要求变量必须有值，所以要单独获取一次
        List<PurchaseDetailEntity> collect = items.stream().filter(i->{// 【已完成】 确认采购单状态是0或1才能合并【老师布置任务，已完成】
            if(!(purchaseDetailService.getById(i).getStatus() == WareConstant.DetailStatus.CREATED.getCode() || purchaseDetailService.getById(i).getStatus() == WareConstant.DetailStatus.ASSIGNED.getCode())){
                System.out.println("订单id【" + i + "】已在采购中，不能合并，将合并其余未在采购中的订单");
                return false;
            }
            return true;
        }).map(i -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(i);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            // TODO 未设置仓库
            purchaseDetailEntity.setStatus(WareConstant.PurchaseStatus.ASSIGNED.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(collect);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }

    @Override
    public void received(List<Long> ids) {
        // 1. 确认当前采购单是否时新建或者已分配的状态【本人按照只有已分配才进行派单修改】
        List<PurchaseEntity> collect = ids.stream().map(id -> {
            PurchaseEntity byId = this.getById(id);
            return byId;
        }).filter(item -> {//过滤新建或已分配的采购单
//            if (item.getStatus() == WareConstant.PurchaseStatus.CREATED.getCode() || item.getStatus() == WareConstant.PurchaseStatus.ASSIGNED.getCode()) {
            if (item.getStatus() == WareConstant.PurchaseStatus.ASSIGNED.getCode()) {//【只是新建没有分配采购人员的不进行接单】
                return true;
            }
            return false;
        }).map(item->{//将新建或已分配的采购单状态改为已领取
            item.setStatus(WareConstant.PurchaseStatus.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        // 2. 改变采购单的状态
        this.updateBatchById(collect);

        // 3. 改变采购项的状态
        collect.forEach(item->{
            List<PurchaseDetailEntity> entityList = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> detailEntities = entityList.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.DetailStatus.BUYING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(detailEntities);
        });

    }

    @Transactional
    @Override
    public void done( PurchaseDoneVo purchaseDoneVo) {
        // 1. 改变采购项的状态
        // TODO 应该判断当前采购项是否为正在采购状态，或者当前采购订单是否为已领取状态
        Boolean flag = true;
        List<PurchaseItemDoneVo> items = purchaseDoneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<>();//采购订单
        for(PurchaseItemDoneVo item : items){
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();//各个采购项
            if(item.getStatus() == WareConstant.DetailStatus.HAS_ERROR.getCode()){//采购失败
                flag = false;
                detailEntity.setStatus(item.getStatus());
            }else {
                detailEntity.setStatus(WareConstant.DetailStatus.FINISH.getCode());//采购成功
                // 3. 采购成功的商品(项）进行入库 更新`wms_ware_sku`表
                PurchaseDetailEntity entity = purchaseDetailService.getOne(new QueryWrapper<PurchaseDetailEntity>().eq("sku_id",item.getItemId()).eq("purchase_id", purchaseDoneVo.getId()));//当前采购项的详细信息

                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());//TODO 将指定商品的指定数量，保存到指定仓库【缺少一项信息，商品名称，需要从gulimall-product中查】
                detailEntity.setId(entity.getId());
            }
            detailEntity.setPurchaseId(purchaseDoneVo.getId());
            detailEntity.setSkuId(item.getItemId());
            updates.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(updates);//批量更新采购项


        // 2. 改变采购单的状态[只要有一个采购项不成功flag=false，则该采购单以有异常表示]
        Long id = purchaseDoneVo.getId();//采购单id
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(id);
        purchaseEntity.setStatus(flag ? WareConstant.PurchaseStatus.FINISH.getCode() : WareConstant.PurchaseStatus.HAS_ERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }
}