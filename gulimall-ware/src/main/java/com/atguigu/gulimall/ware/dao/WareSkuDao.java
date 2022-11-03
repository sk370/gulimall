package com.atguigu.gulimall.ware.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 商品库存
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:12:48
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
    /**
     * 自定义方法
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    /**
     * 自定义查询
     * @param skuId
     * @return
     */
    Long getSkuStock(@Param("skuId") Long skuId);
}
