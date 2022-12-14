package com.atguigu.gulimall.ware.vo;

import java.util.List;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className MergeVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/25 09:21
 */
@Data
public class MergeVo {
    private Long purchaseId;
    private List<Long> items;
}
