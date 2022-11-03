package com.atguigu.gulimall.product.vo;

import java.util.List;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SpuItemAttrGroupVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/01 16:36
 */
@Data
public class SpuItemAttrGroupVo{
    private String groupName;
    private List<SpuBaseAttrVo> attrs;
}
