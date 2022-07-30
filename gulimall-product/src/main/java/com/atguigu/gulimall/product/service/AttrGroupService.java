package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 属性分组
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

