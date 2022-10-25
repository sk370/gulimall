package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className PurchaseDoneVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/25 12:25
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id;//采购单id

    private List<PurchaseItemDoneVo> items;//采购项
}
