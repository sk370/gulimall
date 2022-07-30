package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
