package com.atguigu.common.to;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SpuBoundTo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/24 16:03
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
