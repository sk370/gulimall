package com.atguigu.gulimall.member.dao;

import org.apache.ibatis.annotations.Mapper;

import com.atguigu.gulimall.member.entity.MemberCollectSpuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 会员收藏的商品
 * 
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
@Mapper
public interface MemberCollectSpuDao extends BaseMapper<MemberCollectSpuEntity> {
	
}
