package com.atguigu.gulimall.thirdparty.component;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className SMSComponent
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 17:24
 */
@Component
public class SMSComponent {
    @Value("${spring.cloud.alicloud.sms.accessKeyId}")
    private String accessKeyId;
    @Value("${spring.cloud.alicloud.sms.accessKeySecret}")
    private String accessKeySecret;
    @Value("${spring.cloud.alicloud.sms.endpoint}")
    private  String endpoint;
    @Value("${spring.cloud.alicloud.sms.signName}")
    private String signName;
    @Value("${spring.cloud.alicloud.sms.templateCode}")
    private String templateCode;

    public void sendSmsCode(String phone, String code){
        // 1. 初始化账号信息
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret)
                .setEndpoint(endpoint);
        Client client = null;
        try {
            client = new Client(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 指定模板、签名、手机号、验证码
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setSignName(signName);
        sendSmsRequest.setTemplateCode(templateCode);
        sendSmsRequest.setPhoneNumbers(phone);
        sendSmsRequest.setTemplateParam("{\"code\":\"" + code + "\"}");

        // 3. 发送短信
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        int statusCode;
        try {
            SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtimeOptions);
//            System.out.println(sendSmsResponse.toString() + "+++++++++++++++++++++++++++++++++");
            statusCode = sendSmsResponse.statusCode;
//            System.out.println(statusCode + "+++++++++++++++++++++++++++++++++");
        } catch (TeaException error) {
            String errMessage = Common.assertAsString(error.message);
            System.out.println("+++++++++++++++++++++TeaException" + errMessage);
        } catch (Exception e) {
            TeaException error = new TeaException(e.getMessage(), e);
            String errMessage = Common.assertAsString(error.message);
            System.out.println("+++++++++++++++++++++Exception" + errMessage);
        }
    }
}
