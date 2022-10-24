package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义保存方法
     * @param categoryBrandRelation
     */
    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     * 自定义更新方法，被关联时的更新
     * @param brandId
     * @param name
     */
    void updateBrand(Long brandId, String name);

    /**
     * 自定义更新方法，被关联时的更新
     * @param catId
     * @param name
     */
    void updateCategory(Long catId, String name);

    /**
     * 自定义查询方法：
     * @param catId
     * @return
     */
    List<BrandEntity> getBrandsByCatId(Long catId);
}

