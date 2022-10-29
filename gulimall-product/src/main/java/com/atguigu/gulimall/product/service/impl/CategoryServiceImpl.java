package com.atguigu.gulimall.product.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
//    可以重新定义，也可以使用ServlcieImpl的范型CategoryDao，在ServiceImpl中定义了M类型的实体baseMapper，继承自CategoryDao，由于M类是CategoryDao的子类，所以可以直接通过M调用CategoryDao的方法
//    @Autowired
//    CategoryDao categoryDao;
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

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
        //TODO 1.检查当前要删除的商品分类，是否被其他地方引用

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
    @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'")//删除指定缓存，注意普通字符串要加单引号
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);//更新`pms_category`表
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());//更新pms_category_brand_relation`表
    }

//    @Cacheable(value = {"category"}, key = "'level1Categorys'")//指定当前方法的结果需要缓存。如果缓存中有，方法都不用调用。缓存中没有才会执行方法，并将方法的结果自动存入缓存。
    @Cacheable(value = {"category"}, key = "#root.method.name")//使用方法名作为key名。
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> entities = this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return entities;
    }

    /**
     * 抽取查询方法（从指定范围，查找其父级为parent_cid的子分类）
     * @param selectList
     * @param parent_cid
     * @return
     */
    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> {
            return item.getParentCid() == parent_cid;//当前分类的父id为parent_cid
        }).collect(Collectors.toList());
        return collect;
    }

    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDBWithRedissonLock() {
        // 1. 从redisson获取锁
        RLock lock = redissonClient.getLock("catalogJson-lock");//锁的名字代表锁的粒度，越细越快，一般约定:具体缓存的某个数据
        lock.lock();
        Map<String, List<Catelog2Vo>> dataFromDB = null;
        try {
            dataFromDB = getDataFromDB();
        }finally {
            lock.unlock();
        }
        return dataFromDB;
    }

    private Map<String, List<Catelog2Vo>> getDataFromDB() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");//从缓存中获取，防止高并发场景下，前一个请求已经访问了数据库

        if(!StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});
            return result;
        }

        List<CategoryEntity> selectList = baseMapper.selectList(null);//查询所有分类

        // 1. 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2. 封装数据
        Map<String, List<Catelog2Vo>> collect = level1Categorys.stream().collect(Collectors.toMap(k -> {
            return k.getCatId().toString();//一级分类的id作为key
        }, v -> {
            // 2.1 根据1级分类id查询二级分类
            List<CategoryEntity> entities = getParent_cid(selectList, v.getCatId());
            // 2.2 封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (entities != null) {
                catelog2Vos = entities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 2.3 根2级分类id查询3级分类
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());
                    List<Catelog2Vo.Catelog3Vo> catelog3Vos = null;
                    if(level3Catelog != null){
                        catelog3Vos = level3Catelog.stream().map(l3 -> {
                            Catelog2Vo.Catelog3Vo catelog3Vo = new Catelog2Vo.Catelog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName().toString());
                            return catelog3Vo;
                        }).collect(Collectors.toList());
                    }
                    catelog2Vo.setCatalog3List(catelog3Vos);

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));

        String jsonString = JSON.toJSONString(collect);//将对象转换为json字符串，用于存入redis【之所以不采用对象序列化的方式，原因是转换为json后其他语言也可以使用】
        redisTemplate.opsForValue().set("catalogJSON", jsonString, 1, TimeUnit.DAYS);//给缓存设置数据[设置过期时间，解决穿透、雪崩问题]

        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");//从缓存中获取

        if(StringUtils.isEmpty(catalogJSON)) {
            Map<String, List<Catelog2Vo>> catalogJsonFromDB = getCatalogJsonFromDBWithRedissonLock();//redis中没有才去数据库中查询
            return catalogJsonFromDB;
        }

        Map<String, List<Catelog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catelog2Vo>>>(){});// TypeReference使用了protected修饰，所以这里使用了匿名内部类的方式
        return result;
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