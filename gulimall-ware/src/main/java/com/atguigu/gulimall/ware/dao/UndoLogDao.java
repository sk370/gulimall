package com.atguigu.gulimall.ware.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.ware.entity.UndoLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:12:48
 */
@Mapper
public interface UndoLogDao extends BaseMapper<UndoLogEntity> {
	
}
