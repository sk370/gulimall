package com.atguigu.common.exception;

/**
 * 锁定库存失败时抛出的异常
 * @author zhuyuqi
 * @version v0.0.1
 * @className NoStockException
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/08 22:41
 */
public class NoStockException extends RuntimeException{
    private Long skuId;
    public NoStockException(Long skuId){
        super("商品id为" + skuId + "没有足够的库存");
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }
}
