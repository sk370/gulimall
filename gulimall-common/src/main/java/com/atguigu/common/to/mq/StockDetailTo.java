package com.atguigu.common.to.mq;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className StockDetailTo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/10 09:20
 */
@Data
public class StockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;
}
