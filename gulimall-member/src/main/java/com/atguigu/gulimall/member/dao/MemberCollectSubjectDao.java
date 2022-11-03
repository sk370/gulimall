package com.atguigu.gulimall.member.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.member.entity.MemberCollectSubjectEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 会员收藏的专题活动
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
@Mapper
public interface MemberCollectSubjectDao extends BaseMapper<MemberCollectSubjectEntity> {
	
}
