package com.atguigu.gulimall.order.listener;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.pay.PayAsyncVo;

/**
 * 监听支付宝支付成功的异步回调
 * @author zhuyuqi
 * @version v0.0.1
 * @className OrderPayedListener
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/10 23:29
 */
@RestController//需要给支付宝服务器发送数据
public class OrderPayedListener {
    @Autowired
    OrderService orderService;
    @Autowired
    AlipayTemplate alipayTemplate;

    @PostMapping("payed/notify")
    public String handleAlipayed(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException, UnsupportedEncodingException {
//        Map<String, String[]> parameterMap = request.getParameterMap();
//        System.out.println("支付宝通知到位了，数据为：" + parameterMap);
        // 验证签名，确定是支付宝返回的信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayTemplate.getAlipay_public_key(), alipayTemplate.getCharset(), alipayTemplate.getSign_type()); //调用SDK验证签名
        if (signVerified) {
            System.out.println("签名验证成功~~~~~~~~~~~~~~");
            String result = orderService.handlePayResult(vo, request);//处理支付结果
            return result;//支付宝要求成功返回success
        } else {
            return "error";
        }
    }
}
