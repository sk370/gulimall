package com.atguigu.common.to;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SkuHasStockVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 08:59
 */
@Data
public class SkuHasStockVo {
    private Long skuId;
    private Boolean hasStock;
}
