package com.atguigu.gulimallcart.controller.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import com.atguigu.gulimallcart.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimallcart.controller.service.CartService;
import com.atguigu.gulimallcart.feign.ProductFeignService;
import com.atguigu.gulimallcart.interceptor.CartInterceptor;
import com.atguigu.gulimallcart.to.UserInfoTo;
import com.atguigu.gulimallcart.vo.CartItem;
import com.atguigu.gulimallcart.vo.SkuInfoEntity;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className CartServiceImpl
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/05 13:38
 */
@Service
public class CartServiceImpl implements CartService {
    private final String CART_PREFIX = "gulimall:cart:";//存入redis的键，临时购物车和登录购物车使用相同前缀
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {
        // 1. 得到用户信息
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());//由于存入的时候进行了json转化，所以可以转为字符串
        if(StringUtils.isEmpty(o)){//购物车无当前添加的商品

            CartItem cartItem = new CartItem();
            // 2. 封装cartitem——添加新商品到购物车
            cartItem.setSkuId(skuId);
            cartItem.setCount(num);
            // 2.1 远程查询商品信息
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                R info = productFeignService.getSkuInfo(skuId);
                SkuInfoEntity skuInfoEntity = info.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
                });
                cartItem.setCheck(true);
                cartItem.setImage(skuInfoEntity.getSkuDefaultImg());
                cartItem.setTitle(skuInfoEntity.getSkuTitle());
                cartItem.setPrice(skuInfoEntity.getPrice());
            }, executor);
            // 2.2 远程查询商品的属性信息
            CompletableFuture<Void> getSkuAttrs = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValues = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(skuSaleAttrValues);
            }, executor);
            CompletableFuture.allOf(getSkuInfo,getSkuAttrs).get();

            // 3. 将商品信息添加到redis（用于联通未登录添加购物车和登陆后查看购物车）
            String s = JSON.toJSONString(cartItem);//如果没有这句，则CartItem需要实现序列化接口
            cartOps.put(skuId.toString(), s);//存入redis
            return cartItem;
        } else {//当前商品已经在购物车中有，只需要改变数量
            CartItem item = JSON.parseObject(o, CartItem.class);
            item.setCount(item.getCount() + num);
            String s = JSON.toJSONString(item);//如果没有这句，则CartItem需要实现序列化接口
            cartOps.put(skuId.toString(), s);//存入redis
            return item;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        // 获取reids操作对象
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String o = (String) cartOps.get(skuId.toString());
        CartItem cartItem = JSON.parseObject(o, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if(userInfoTo.getUserId() != null){//已登录用户
            cartKey = CART_PREFIX + userInfoTo.getUserId();
            // 合并临时购物车
            // 1. 临时购物车
            List<CartItem> tempCartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());//获取临时购物车的数据
            if(tempCartItems != null){
                for (CartItem tempCartItem : tempCartItems)
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());//由于当前是一登陆用户，所以addToCart会自动添加到登录后的购物车中
            }
            // 2. 登陆后既有购物车[此时已包含临时购物车]
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
            clearCart(CART_PREFIX + userInfoTo.getUserKey());//清空临时购物车
        }else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            cart.setItems(cartItems);
        }
        return cart;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);//删除键
    }

    @Override
    public void checkItem(String skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        // 获取当前购物项
        CartItem cartItem = getCartItem(Long.parseLong(skuId));
        cartItem.setCheck(check == 1 );
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId, s);
    }

    @Override
    public void changeItemCount(String skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        // 获取当前购物项
        CartItem cartItem = getCartItem(Long.parseLong(skuId));
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId, s);
    }

    @Override
    public void deleteItem(String skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    /**
     * 获取到要操作的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        // 1. 得到用户信息
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if(userInfoTo.getUserId() != null){//已登录用户
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        }else {
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }

//        redisTemplate.opsForHash().get(cartKye, 1);//获取购物车中的1号数据
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);//绑定指定key的操作
        return operations;
    }

    /**
     * 获取指定key的所有数据
     * @param cartKey
     * @return
     */
    private List<CartItem> getCartItems(String cartKey){
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);//绑定指定key的操作
        List<Object> values = operations.values();//获取到所有的购物项
        if(values != null && values.size() > 0){
            List<CartItem> collect = values.stream().map(obj -> {
                String str = (String) obj;
                CartItem cartItem = JSON.parseObject(str, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

}
