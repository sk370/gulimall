package com.atguigu.gulimall.product.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrAttrgroupRelationDao relationDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);//保存基本数据

        //保存关联关系[当前为销售属性时不需要保存]
        if(attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null){//1表示规格参数
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());//attrType=base表示规格参数，=sale表示销售属性
        if(catelogId != 0){
            queryWrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((wrapper) ->{
                wrapper.eq("attr_id", key).or().like("attr_name",key);
            });
        }
        IPage<AttrEntity> page = this.page(
            new Query<AttrEntity>().getPage(params),
            queryWrapper
        );

        PageUtils pageUtils = new PageUtils(page);//获取最开始的page对象【无分组和分类的属性名】

        List<AttrEntity> records = page.getRecords();//获取page对象中的记录数据，进行增强
        List<AttrRespVo> attrRespVoList = records.stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            // 设置分类和分组名字【attrType=base表示规格参数时才设置分类，否则不设置】
            if(Objects.equals("base", attrType)) {
                AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));//从关联关系的表中查出
                if (!StringUtils.isEmpty(relationEntity) && relationEntity.getAttrGroupId() != null) {
                    Long attrGroupId = relationEntity.getAttrGroupId();
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrGroupId);
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (!StringUtils.isEmpty(categoryEntity)) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(attrRespVoList);//将增强后的数据内容【attrRespVoList】存放入page对象中

        return pageUtils;
    }

    @Cacheable(value = "attr", key = "'attrInfo:' + #root.args[0]")//调用结果存入缓存，避免远程方法每次调用耗时
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        //1. 设置分组信息
        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelation = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));//从`pms_attr_attrgroup_relation`表获取分组信息
            if (attrAttrgroupRelation != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelation.getAttrGroupId());//1.1 设置分类id
                AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelation.getAttrGroupId());
                if (attrGroupEntity != null) {
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());//1.2 设置分类名
                }
            }
        }
        // 2. 设置分类信息
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        if(categoryEntity != null){
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }
    @Transactional
    @Override
    public void updateAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr,attrEntity);
        this.updateById(attrEntity);//修改`pms_attr`表
        //1. 修改`pms_attr_attrgroup_relation`表

        if(attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            Integer selectCount = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));//检查当前属性是否有关联的分组，有则更新，无则新增
            if (selectCount > 0) {
                relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
            } else {
                relationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据分组id查找关联关系
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));//查找`pms_attr_attrgroup_relation`表
        List<Long> attrIdList = relationEntities.stream().map((attr) -> {
            return attr.getAttrId();
        }).collect(Collectors.toList());
        if(attrIdList == null || attrIdList.size() == 0){
            return null;
        }
        Collection<AttrEntity> attrEntities = this.listByIds(attrIdList);//查找`pms_attr`表
        return (List<AttrEntity>)attrEntities;
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
//        relationDao.delete(new QueryWrapper<>().eq("attr_id", 1L).eq("attr_group_id", 1L));
        List<AttrAttrgroupRelationEntity> relationEntityList = Arrays.asList(vos).stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(relationEntityList);
    }

    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1. 当前分组只能关联自己所属分类里的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();

        // 2. 当前分组只能关联没有被其他分组关联的属性
        // 2.1 当前分类下的所有分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>()
                                            .eq("catelog_id", catelogId));
        List<Long> collect = attrGroupEntities.stream().map((item) -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());
        //2.2 查找其他分组已关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> longList = relationEntities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        // 2.3 从当前分类的所有属性中移除这些属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(longList != null && longList.size() > 0){
            queryWrapper.notIn("attr_id", longList);
        }
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            queryWrapper.and((w) -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Override
    public List<Long> selectSearchAttrs(List<Long> attrIds) {
        // SELECT attr_id FROM `pms_attr` WHERE attr_id IN (attrIds) AND search_TYPE = 1

        return this.baseMapper.selectSearchAttrIds(attrIds);
    }

}