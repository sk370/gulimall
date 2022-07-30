package com.atguigu.gulimall.member.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.GrowthChangeHistoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

