package com.atguigu.gulimall.member.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 会员等级
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {
    /**
     * 自定义sql
     * @return
     */
    MemberLevelEntity getDefaultLevel();
}
