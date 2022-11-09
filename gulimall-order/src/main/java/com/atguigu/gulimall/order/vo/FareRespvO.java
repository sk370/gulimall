package com.atguigu.gulimall.order.vo;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className FareRespvO
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 14:40
 */
@Data
public class FareRespvO {
    private MemberAddressVo address;
    private BigDecimal fare;
}
