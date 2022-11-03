package com.atguigu.gulimall.product.service;

import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品评价回复关系
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

