package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.entity.SpuInfoDescEntity;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuImagesService imagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");//关键字查询
        String catelogId = (String) params.get("catelogId");
        String brandId = (String) params.get("brandId");
        String min = (String) params.get("min");//状态
        String max = (String) params.get("max");//状态
        if(!StringUtils.isEmpty(params.get("key"))){//是否有关键字
            wrapper.and((w) ->{
                w.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        if(!StringUtils.isEmpty(params.get("catelogId")) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }
        if(!StringUtils.isEmpty(params.get("brandId"))&& !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        if(!StringUtils.isEmpty(params.get("min"))){
            wrapper.ge("price", min);//ge 大于等于 gt 大于
        }
        if(!StringUtils.isEmpty(params.get("max"))){
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
                if(bigDecimal.compareTo(new BigDecimal("0")) == 1){
                    wrapper.le("price", max);//le 小于等于 lt 小于
                }
            } catch (Exception e){

            }
        }

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params),wrapper);

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> skuInfoEntityList = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skuInfoEntityList;
    }

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        // 1. sku基本信息获取 `pms_sku_info`表
        SkuInfoEntity info = getById(skuId);
        Long catalogId = info.getCatalogId();
        Long spuId = info.getSpuId();
        skuItemVo.setInfo(info);

        // 2. sku图片信息 `pms_sku_images`表
        List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
        skuItemVo.setImages(images);

        // 3. 获取spu的销售属性组合——根据spu查找属性类型为销售属性的属性的组合
      /*
        选择颜色
            石墨色12promax 6.7英寸港版石墨色12promax 6.7英寸港版
            金色12promax 6.7英寸港版金色12promax 6.7英寸港版
            海蓝色12promax 6.7英寸港版海蓝色12promax 6.7英寸港版
            银色12promax 6.7英寸港版银色12promax 6.7英寸港版
            海蓝色 12pro 6.1英寸海蓝色 12pro 6.1英寸
            金色 12pro 6.1英寸金色 12pro 6.1英寸
            石墨色 12pro 6.1英寸石墨色 12pro 6.1英寸
            银色 12pro 6.1英寸银色 12pro 6.1英寸
        选择版本
            128GB未激活
            256GB未激活
            512GB未激活
        */
        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVo.setSaleAttr(saleAttrVos);

        // 4. 获取spu的介绍 `pms_spu_info_desc`
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDesp(spuInfoDescEntity);

        // 5. 获取spu的规格参数信息
        /*
        主体      机型       Apple iPhone 12 Pro Max
                上市日期    2020-11-05
               入网型号     1
        基本信息    机身尺寸    宽78.1mm；长160.8mm；厚7.4mm
                   CPU型号     A14
         */
        List<SpuItemAttrGroupVo> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        skuItemVo.setGroupAttrs(groupAttrs);

        return skuItemVo;
    }

}