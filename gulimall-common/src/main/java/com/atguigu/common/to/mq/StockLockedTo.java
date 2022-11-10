package com.atguigu.common.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className StockLockedTo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/10 09:08
 */
@Data
public class StockLockedTo {
    private Long id;//库存工作单id（`wms_ware_order_task`）
    private StockDetailTo detailTo;//工作单详情
}
