package com.atguigu.gulimall.order.web;

import com.atguigu.common.exception.NoStockException;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className OrderWebController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 16:25
 */
@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;

    /**
     * 确认订单
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/toTrade")
    public String getHtml(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData",confirmVo);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        // 点击下单：完成订单创建、验证令牌、价格确认、锁定库存等一系列操作。
        // 下单成功去往支付页面
        // 下单失败返回订单页
        SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
        try {
            responseVo = orderService.submitOrder(vo);
        } catch (NoStockException e) {
            responseVo.setCode(3);
            e.printStackTrace();
        } catch (RuntimeException e) {
            responseVo.setCode(2);
            e.printStackTrace();
        }finally {
            if(responseVo.getCode() == 0){//下单成功
                model.addAttribute("submitOrderResp",responseVo);
                return "pay";
            }else {
                String msg = "下单失败";
                switch (responseVo.getCode()){
                    case 1:
                        msg += "，订单信息过期，请刷新页面重新提交。";
                        break;
                    case 2:
                        msg += "，订单商品价格发生变化，请确认后再次提交";
                        break;
                    case 3:
                        msg += "，库存锁定失败，商品库存不足。";
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        }

    }
}
