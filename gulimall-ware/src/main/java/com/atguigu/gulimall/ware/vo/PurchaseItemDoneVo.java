package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * 采购项
 * @author zhuyuqi
 * @version v0.0.1
 * @className PurchaseItemDoneVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/25 12:26
 */
@Data
public class PurchaseItemDoneVo {
    private Long itemId;
    private Integer status;
    private String reason;
}
