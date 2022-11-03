package com.atguigu.gulimall.product.service;

import java.util.List;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 属性&属性分组关联
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义批量保存关联关系
     * @param vos
     */
    void saveBath(List<AttrGroupRelationVo> vos);
}

