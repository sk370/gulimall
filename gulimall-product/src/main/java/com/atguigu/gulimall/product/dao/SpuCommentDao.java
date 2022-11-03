package com.atguigu.gulimall.product.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.product.entity.SpuCommentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 商品评价
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
@Mapper
public interface SpuCommentDao extends BaseMapper<SpuCommentEntity> {
	
}
