package com.atguigu.gulimall.ware.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * 同gulimall-cart的cartitem
 * @author zhuyuqi
 * @version v0.0.1
 * @className OrderItemVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 21:59
 */
@Data
public class OrderItemVo {
    private Long skuId;
//    private Boolean check = true;//不需要了
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;
//    private boolean hasStock;//boolean默认为false，Boolean默认为null，所以为了避免出现空指针，这里设置了boolean
    private Map<Long,Boolean> stocks;
    private BigDecimal weight;//商品重量，用于运费计算
}
