package com.atguigu.gulimall.member.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberCollectSpuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

