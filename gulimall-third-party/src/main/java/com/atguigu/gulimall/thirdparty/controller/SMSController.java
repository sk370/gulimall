package com.atguigu.gulimall.thirdparty.controller;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdparty.component.SMSComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信验证码
 * @author zhuyuqi
 * @version v0.0.1
 * @className SMSController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 14:38
 */

@RestController
@RequestMapping("/sms")
public class SMSController {
    @Autowired
    SMSComponent smsComponent;

    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        smsComponent.sendSmsCode(phone,code);
        return R.ok();
    }

}
