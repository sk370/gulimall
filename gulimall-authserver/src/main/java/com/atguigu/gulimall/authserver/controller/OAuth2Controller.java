package com.atguigu.gulimall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.authserver.feign.MemeberFeignService;
import com.atguigu.gulimall.authserver.po.WeiboAcctPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;

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
    public String weibo(@RequestParam("code") String code, HttpSession session){
        System.out.println(code + " ~~~~~~~~~~~~~~~~~~~~~~~~");

        // 模拟表单发送post请求
        // 请求头设置,x-www-form-urlencoded格式的数据
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //提交参数设置
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "1712672930");//
        map.add("client_secret", "cc05412d8ba365492acdeab0a27cd1ae");//
        map.add("grant_type", "authorization_code");//授权方式
        map.add("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");//后续的请求处理地址
        map.add("code",code);//后续的请求处理地址

        // 组装请求体
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        // 发送post请求，并打印结果
        String url = "https://api.weibo.com/oauth2/access_token";
        WeiboAcctPo weiboAcctPo = restTemplate.postForObject(url, request, WeiboAcctPo.class);
        System.out.println(weiboAcctPo + "~~~~~~~~~~~~~~~~~~~~~~~~");

        if(weiboAcctPo != null){
            // 查询数据库，判断是否是第一次登录，如果是，同时进行注册
//            result 转换为weiboAcctPo
            R r = memeberFeignService.oauthLogin(weiboAcctPo);
            if(r.getCode() == 0) {
                MemberRespVo memberRespVo = r.getData("msg", new TypeReference<MemberRespVo>() {
                });
                System.out.println(memberRespVo + "~~~~~~~~~~~~~~~~~~~~~~~~");
                session.setAttribute(AuthServerConstant.LOGIN_USER, memberRespVo);
                return "redirect:http://gulimall.com";
            }else{
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else{

            return "redirect:http://auth.gulimall.com/login.html";
        }
    }
}
