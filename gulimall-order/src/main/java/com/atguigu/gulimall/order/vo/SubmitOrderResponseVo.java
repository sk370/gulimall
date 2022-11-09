package com.atguigu.gulimall.order.vo;

import com.atguigu.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SubmitOrderResponseVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 17:08
 */
@Data
public class SubmitOrderResponseVo {
    private OrderEntity order;
    private Integer code;//0成功，错误状态码

}
