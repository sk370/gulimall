package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 详情页控制器方法
 * @author zhuyuqi
 * @version v0.0.1
 * @className ItemController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/01 14:43
 */
@Controller
public class ItemController {
    @Autowired
    SkuInfoService skuInfoService;
    /**
     * 展示当前商品的详情页
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model){
        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        System.out.println("准备查询" + skuId + "的详情");
        return "item";
    }
}
