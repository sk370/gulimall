package com.atguigu.gulimallcart.controller;

import com.atguigu.gulimallcart.service.CartService;
import com.atguigu.gulimallcart.interceptor.CartInterceptor;
import com.atguigu.gulimallcart.to.UserInfoTo;
import com.atguigu.gulimallcart.vo.Cart;
import com.atguigu.gulimallcart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className CartController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/05 11:21
 */
@Controller
public class CartController {
    @Autowired
    CartService cartService;

    /**
     * 获取redis的购物项（里面重新查询了价格）
     * @return
     */
    @ResponseBody
    @GetMapping("/currentUserCartItems")
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }

    /**
     * 删除reids中商品的数量
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem.html")
    public String deleteItem(@RequestParam("skuId") String skuId){
        cartService.deleteItem(skuId);
//        return "redirect:http://cart.gulimall.com/cart.html";
        return "forward:cart.html";
    }

    /**
     * 修改reids中商品的数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem.html")
    public String countItem(@RequestParam("skuId") String skuId, @RequestParam("count") Integer num){
        cartService.changeItemCount(skuId, num);
//        return "redirect:http://cart.gulimall.com/cart.html";
        return "forward:cart.html";
    }

    /**
     * 修改reids中商品选中状态
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem.html")
    public String checkItem(@RequestParam("skuId") String skuId, @RequestParam("check") Integer check){
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
//        return "cart.html";
    }

    /**
     *  购物车列表
     * @return
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {
        // 1. 快速得到用户信息：id、userkey
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 加入购物车
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes ra) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId, num);
//        model.addAttribute("item",cartItem);
        ra.addAttribute("skuId", skuId);
//        return "success";//直接返回造成刷新页面重复提交数据问题，所以使用重定向解决
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    /**
     * 解决重复提交的问题【刷新页面总是进行查询，而不是提交】
     * @param skuId
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId, Model model){
        // 查询页面
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }
}
