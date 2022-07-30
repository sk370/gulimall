package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberLoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
