package com.atguigu.gulimall.product.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.to.SkuReductionTo;
import com.atguigu.common.to.SpuBoundTo;
import com.atguigu.common.to.es.SkuESModel;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.dao.SpuInfoDao;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    AttrService attrService;
    @Autowired
    ProductAttrValueService productAttrValueService;
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    WareFeignService wareFeignService;
    @Autowired
    SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    //TODO 高级课程完成保存失败时的各类处理，以及事务机制
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        // 1. 保存spu基本信息：`pms_spu_info`表
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfoEntity);

        // 2. 保存spu的描述图片：`pms_spu_images`表
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        // 3. 保存spu的描述信息：`pms_spu_info_desc`表
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        spuInfoDescEntity.setDecript(String.join(",", decript));//将decript中的string按，拼接
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        // 4. 保存spu的规格参数：`pms_product_attr_value`表
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            productAttrValueEntity.setAttrName(attrEntity.getAttrName());
            productAttrValueEntity.setAttrValue(attr.getAttrValues());
            productAttrValueEntity.setQuickShow(attr.getShowDesc());
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveProductAttr(collect);

        // 5. 保存spu的积分信息：gulimall-sms 》`sms_spu_bounds`表//远程方法调用
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfoEntity.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0){
            log.error("远程保存spu积分信息失败");
        }

        // 6. 保存当前spu的对应的所有sku信息：
        // 6.1 保存sku的基本信息：`pms_sku_info`表
        List<Skus> skus = vo.getSkus();
        if(skus != null && skus.size() > 0){
            skus.forEach(item -> {
                String defaultImg = "";
                for(Images image : item.getImages()){
                    if(image.getDefaultImg() == 1){
                        defaultImg = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDesc(String.join(",", item.getDescar()));//自己写的，不知道对不对
                skuInfoEntity.setSaleCount(0L);
//                skuInfoEntity.setSkuDesc(item.gets);
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfoEntity);//保存基本信息到pms_sku_info表，此时只保存了默认图片，其他图片还没保存

                // 6.2 保存sku的图片信息： `pms_sku_images`
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    return !StringUtils.isEmpty(entity.getImgUrl());//返回true就是需要，false剔除。没有图片路径的不进行保存
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(imagesEntities);

                // 6.3 保存sku的销售属性信息：`pms_sku_sale_attr_value`
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // 6.4 保存sku的优惠、满减等信息：gulimall-sms 》 `sms_sku_ladder`表 和 `sms_sku_full_reduction`表 和 `sms_member_price`表
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if(skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1){
                    R r1 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if(r1.getCode() != 0){
                        log.error("远程保存sku优惠信息失败");
                    }
                }
            });
        }




    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity) {
//        this.save(spuInfoEntity);//这句应该和下句等价，只是这个是service层方法
        this.baseMapper.insert(spuInfoEntity);//this.baseMapper等价于SpuInfoDao
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");//关键字查询
        String status = (String) params.get("status");//状态
        String brandId = (String) params.get("brandId");
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(params.get("key"))){//是否有关键字
            wrapper.and((w) ->{
                w.eq("id", key).or().like("spu_name", key);
            });
        }
        if(!StringUtils.isEmpty(params.get("status"))){
            wrapper.eq("publish_status", status);
        }
        if(!StringUtils.isEmpty(params.get("brandId"))  && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id", brandId);
        }
        if(!StringUtils.isEmpty(params.get("catelogId")) && !"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id", catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        // 1. 组装需要的数据
        // 1.1 查出当前spuid对应的所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        // 【已完成】 查询当前sku的所有可以用来被检索的规格属性（由于不同的sku具有相同的规格，所以只需要按照同种spu查询一遍，不需要每个sku都查）
        List<ProductAttrValueEntity> productAttrValueEntities;//获取当前spu对应的所有attr（sku的attr继承自spu）
        productAttrValueEntities = productAttrValueService.baseAttrListForSpu(spuId);
        // 【已完成】 当前商品没有设置规格参数，即`pms_product_attr_value`表没有spuId属性，查到对象则为空
        List<Long> attrIds = productAttrValueEntities.stream().map(attr -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());//所有属性的id
        List<Long> searchAttrIds = attrService.selectSearchAttrs(attrIds);//只要有`pms_attr`表中search_type为1的表示可以检索，查找这些属性的id（当前查到的是所有可检索的）
        Set<Long> idSet = new HashSet<>(searchAttrIds);//searchAttrIds去重

        List<SkuESModel.Attrs> attrsList = productAttrValueEntities.stream().filter(item -> {
            return idSet.contains(item.getAttrId());//当前要上架的spu可以被检索的attr
        }).map(item -> {
            SkuESModel.Attrs attrs1 = new SkuESModel.Attrs();
            BeanUtils.copyProperties(item, attrs1);
            return attrs1;
        }).collect(Collectors.toList());

        // 【已完成】 向远程服务查询是否有库存(批量查库存)
        List<Long> skuIdList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long, Boolean> stockMap = null;
        try{
            R r = wareFeignService.getSkuHasStock(skuIdList);
//            Object data1 = r.get("data");//R继承自hashmap，所以data1默认为map类型
//            String s = JSON.toJSONString(data1);
//            List<SkuHasStockVo> data = JSON.parseObject(s, new TypeReference<List<SkuHasStockVo>>() {
//            });//借助fastjson将字符串转换为指定的类型
            stockMap = r.getData(new TypeReference<List<SkuHasStockVo>>(){}).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
        }catch (Exception e){
            log.error("库存查询出现问题，异常原因为{}",e);
        }

        // 1.2 封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;//lambda表达式处理的stockMap可能还是为null，重新设置一遍（即finalStockMap为常量值，指向的地址不会变化），让编译器通过
        List<SkuESModel> upProducts = skus.stream().map(sku -> {
            SkuESModel esModel = new SkuESModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            //保存库存
            if(finalStockMap == null){//查不到数据（网络异常）时默认设置为有库存
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // 【已完成】 热度评分
            esModel.setHotScore(0L);
            // 【已完成】 查询属性信息（品牌的分类和属性、属性值）
            BrandEntity brand = brandService.getById(sku.getBrandId());
            esModel.setBrandName(brand.getName());
            esModel.setCatalogId(sku.getCatalogId());
            esModel.setBrandImg(brand.getLogo());
            CategoryEntity category = categoryService.getById(sku.getCatalogId());
            esModel.setCatalogName(category.getName());

            esModel.setAttrs(attrsList);//设置检索信息

            return esModel;
        }).collect(Collectors.toList());

        // 【已完成】 将所有数据(SkuESModel - collect)发送给ES进行保存-gulimall-search
        R r = searchFeignService.productStatusUp(upProducts);
        if(r.getCode() == 0){//R的构造函数中默认code=0表示成功（没有被修改过）
            // 【已完成】 修改当前spu的状态
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            // TODO 重复调用问题（接口幂等性）重试机制
            log.error("商品上架有失败的~~spuinfoserviceimpl");
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        SkuInfoEntity skuInfoEntity = skuInfoService.getById(skuId);
        Long spuId = skuInfoEntity.getSpuId();
        SpuInfoEntity spuInfoEntity = getById(spuId);
        return spuInfoEntity;
    }

}