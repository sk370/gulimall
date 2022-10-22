package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
//    可以重新定义，也可以使用ServlcieImpl的范型CategoryDao，在ServiceImpl中定义了M类型的实体baseMapper，继承自CategoryDao，由于M类是CategoryDao的子类，所以可以直接通过M调用CategoryDao的方法
//    @Autowired
//    CategoryDao categoryDao;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    /**
     * $$ 测试生成的代码
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * $$ 具体实现分类的组装过程
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1. 查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);
        //2. 组装成父子结构
        //2.1 查找一级分类
        List<CategoryEntity> level1Menus = entities.stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)//一级分类在数据库表中parent_cid为0,lambda简写，省略了（）、{}和return
                .map(menu -> {
                    menu.setChildren(getChildrens(menu, entities));//menu组装好的带子菜单（含孙子菜单）的父菜单：调用自定义的getChildrens方法，再调用实体类的setChildren方法给children属性赋值
                    return menu;//带子菜单、孙子菜单的父菜单
                })
                .sorted((menu1, menu2) -> {//给父菜单进行排序
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());//调用实体类中sort属性/字段的getSrot方法，正序排序
                })
                .collect(Collectors.toList());//最后收集到的是拍好序的父菜单
        return level1Menus;
    }

    /**
     * $$ 查找当前菜单的子菜单
     * @param root 父菜单对象
     * @param all 子菜单（含孙子菜单）对象
     * @return
     */
    public List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream()
                .filter(categoryEntity -> {
                    return categoryEntity.getParentCid() == root.getCatId();//子菜单对象的parentCid与父菜单对象的catId一致
                })
                .map(categoryEntity -> {
                    categoryEntity.setChildren(getChildrens(categoryEntity, all));//categoryEntity装好的带子菜单（不含孙子菜单）的父菜单：调用自定义的getChildrens方法，再调用实体类的setChildren方法给children属性赋值。由于是自己调用自己，所以通过递归实现了孙子菜单的组装
                    return categoryEntity;
                })
                .sorted((categoryEntity1,categoryEntity2) -> {
                    return (categoryEntity1.getSort() == null ? 0 : categoryEntity1.getSort()) - (categoryEntity2.getSort() == null ? 0 : categoryEntity2.getSort());
                })
                .collect(Collectors.toList());//最后收集好的是组装好的带子菜单（含孙子菜单）的父菜单
        return children;
    }

    /**
     * $$
     * @param asList
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前要删除的菜单，是否被其他地方引用

        // 2. 执行逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);//parentPath原本为先子后父，reverse后为先父后子

//        return (Long[])paths.toArray();//回报不能将Object转换为Long的错误
        return (Long[])paths.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有与之有关联的表
     * @param category
     */
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);//更新`pms_category`表
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());//更新pms_category_brand_relation`表
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths){
        paths.add(catelogId);//先存放子元素id，后通过遍历存放父元素id
        CategoryEntity byId = this.getById(catelogId);
        if(byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }
}