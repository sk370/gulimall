package com.atguigu.gulimallcart.controller.service;

import com.atguigu.gulimallcart.vo.Cart;
import com.atguigu.gulimallcart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className CartService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/05 13:38
 */
public interface CartService {
    /**
     * 自定义方法
     * @param skuId
     * @param num
     * @return
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 自定义方法
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 自定义方法：获取整个购物车
     * @return
     */
    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 清空购物车
     * @param cartKey
     */
    void clearCart(String cartKey);

    /**
     * 自定义方法
     * @param skuId
     * @param check
     */
    void checkItem(String skuId, Integer check);

    /**
     * 自定义方法
     * @param skuId
     * @param num
     */
    void changeItemCount(String skuId, Integer num);

    /**
     * 自定义方法：删除指定商品
     * @param skuId
     */
    void deleteItem(String skuId);
}
