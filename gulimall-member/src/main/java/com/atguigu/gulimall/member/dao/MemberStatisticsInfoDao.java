package com.atguigu.gulimall.member.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.member.entity.MemberStatisticsInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 会员统计信息
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:30
 */
@Mapper
public interface MemberStatisticsInfoDao extends BaseMapper<MemberStatisticsInfoEntity> {
	
}
