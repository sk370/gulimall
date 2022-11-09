package com.atguigu.gulimall.ware.vo;

import java.util.List;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className WareSkuLockVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 22:12
 */
@Data
public class WareSkuLockVo {
    private String orderSN;//订单号
    private List<OrderItemVo> locks;//需要锁住的所有商品项库存信息
}
