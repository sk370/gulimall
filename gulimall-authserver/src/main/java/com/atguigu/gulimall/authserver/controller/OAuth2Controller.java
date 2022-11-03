package com.atguigu.gulimall.authserver.controller;

import com.atguigu.gulimall.authserver.feign.MemeberFeignService;
import com.atguigu.gulimall.authserver.po.WeiboAcctPo;
import org.apache.http.HttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 * @author zhuyuqi
 * @version v0.0.1
 * @className OAuth2Controller
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/03 18:27
 */
@Controller
public class OAuth2Controller {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    MemeberFeignService memeberFeignService;

    /**
     * 处理微博登录：前台输入微博账号密码会重定向到该请求，并携带code码
     * @param code
     * @return
     */
    @GetMapping("oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code){
        System.out.println(code + " ~~~~~~~~~~~~~~~~~~~~~~~~");
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "");//未设置
        map.put("client_secret", "");//未设置
        map.put("grant_type", "authorization_code");//授权方式
        map.put("redirect_uri", "http://gulimall.com/oauth2.0/weibo/success");//后续的请求处理地址
        map.put("code",code);//后续的请求处理地址

        HttpEntity request = new BasicHttpEntity();
        WeiboAcctPo weiboAcctPo = restTemplate.postForObject("https://api.weibo.com/oauth2/access_token", request, WeiboAcctPo.class, map);
        System.out.println(weiboAcctPo + "~~~~~~~~~~~~~~~~~~~~~~");

        if(weiboAcctPo != null){
            // 查询数据库，判断是否是第一次登录，如果是，同时进行注册
            memeberFeignService.oauthLogin(weiboAcctPo);
            return "redire:http://gulimall.com";
        } else{

            return "redire:http://auth.gulimall.com/login.html";
        }
    }
}
