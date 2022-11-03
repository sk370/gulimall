package com.atguigu.gulimall.product.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atguigu.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * sku销售属性&值
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {
    /**
     * 自定义sql
     * @param spuId
     * @return
     */
    List<SkuItemSaleAttrVo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);
}
