package com.atguigu.gulimall.product.vo;

import java.util.List;

import com.atguigu.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className AttrGroupWithAttrsVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/24 10:48
 */
@Data
public class AttrGroupWithAttrsVo {

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    private List<AttrEntity> attrs;
}
