package com.atguigu.gulimall.product.vo;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className AttrRespVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/22 17:17
 */
@Data
public class AttrRespVo extends AttrVO{
    private String catelogName;//所属分类名字
    private String groupName;//所属分组名字

    private Long[] catelogPath;//修改时回显的多级分类路径
}
