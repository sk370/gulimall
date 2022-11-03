package com.atguigu.gulimall.ware.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 仓库信息
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:12:48
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
