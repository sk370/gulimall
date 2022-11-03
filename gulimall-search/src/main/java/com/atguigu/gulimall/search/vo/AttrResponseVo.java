package com.atguigu.gulimall.search.vo;

import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className AttrResponseVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/31 22:01
 */
@Data
public class AttrResponseVo {
    private String catelogName;//所属分类名字
    private String groupName;//所属分组名字
    private Long[] catelogPath;//修改时回显的多级分类路径
    /**
     * 属性id
     */
    @TableId
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;
    /**
     * 值类型【0为单个值，1为多个值】
     */
    private Integer valueType;

    private Long attrGroupId;//自定义属性，用于在关联表中保存数据
}
