package com.atguigu.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SkuReductionTo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/24 16:23
 */
@Data
public class SkuReductionTo {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;


}
