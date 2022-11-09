package com.atguigu.gulimall.ware.vo;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className LockStockResultVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 22:16
 */
@Data
public class LockStockResultVo {
    private Long skuId;//
    private Integer num;
    private Boolean locked;
}
