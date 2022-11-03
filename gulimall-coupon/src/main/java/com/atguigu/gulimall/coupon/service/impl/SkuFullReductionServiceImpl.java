package com.atguigu.gulimall.coupon.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atguigu.common.to.MemberPrice;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;
    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // 6.4 保存sku的优惠、满减等信息：gulimall-sms 》 `sms_sku_ladder`表 和 `sms_sku_full_reduction`表 和 `sms_member_price`表
        // sms_sku_ladder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());//满几件
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());//打几折
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());//是否可叠加优惠券
        if(skuReductionTo.getFullCount() > 0){//大于0才向数据库保存
            skuLadderService.save(skuLadderEntity);
        }

        // sms_sku_full_reduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
        if(skuFullReductionEntity.getFullPrice().compareTo(new BigDecimal("0")) == 1 ){
            this.save(skuFullReductionEntity);
        }

        // sms_member_price
        List<MemberPrice> memberPrice = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> collect = memberPrice.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);//默认叠加其他优惠
            return memberPriceEntity;
        }).filter(item ->{
            return item.getMemberPrice().compareTo(new BigDecimal("0")) == 1;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(collect);

    }

}