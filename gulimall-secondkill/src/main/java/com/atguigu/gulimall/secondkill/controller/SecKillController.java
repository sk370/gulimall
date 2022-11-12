package com.atguigu.gulimall.secondkill.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.secondkill.service.SecKillService;
import com.atguigu.gulimall.secondkill.vo.to.SecKillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SecKillController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/11 19:20
 */
@Controller
public class SecKillController {
    @Autowired
    SecKillService secKillService;

    /**
     * 查询当前时间可以参与秒杀的商品
     * @return
     */
    @GetMapping("/currentSecKillSkus")
    @ResponseBody
    public R getCurrentSecKillSkus(){
        List<SecKillSkuRedisTo> vos = secKillService.getCurrentSecKillSkus();
        return R.ok().setData(vos);
    }

    /**
     * 查询当前商品是否有秒杀信息
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("sku/seckill/{skuId}")
    public R getSkuSecKillInfo(@PathVariable("skuId") Long skuId){
        SecKillSkuRedisTo to =  secKillService.getSkuSecKillInfo(skuId);
        return R.ok().setData(to);
    }

    /**
     * 处理秒杀请求
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId, @RequestParam("key") String key, @RequestParam("num") String num, Model model){
        // 1. 判断登录[在拦截器中判断]
        String orderSn = secKillService.kill(killId, key, num);//下单成功返会一个订单号
        model.addAttribute("orderSn",orderSn);
        return "success";
    }
}
