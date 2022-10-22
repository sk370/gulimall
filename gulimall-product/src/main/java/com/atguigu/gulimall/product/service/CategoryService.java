package com.atguigu.gulimall.product.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-29 15:54:23
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * $$ 查出所有分类以及子分类，并以树形结构组装起来
     * @return
     */
    List<CategoryEntity> listWithTree();

    /**
     * $$ 批量删除商品分类
     * @param asList
     */
    void removeMenuByIds(List<Long> asList);

    /**
     * 自定义方法：找到catelogid的完整路径
     * @param catelogId
     * @return
     */
    Long[] findCatelogPath(Long catelogId);

    /**
     * 自定义方法：级联更新
     * @param category
     */
    void updateCascade(CategoryEntity category);
}

