package com.atguigu.gulimall.thirdparty;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.Resource;

import com.atguigu.gulimall.thirdparty.component.SMSComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallThirdPartyApplicationTests {

    @Resource
    OSSClient ossClient;
    @Autowired
    SMSComponent smsComponent;

    @Test
    public void testSMS(){
        smsComponent.sendSmsCode("15771758870", "2585");
    }

    @Test
    public void contextLoads() {
    }

    /**
     * 依赖配置：测试阿里云OOS文件上传
     */
    @Test
    public void testUpload2(){
        String bucketName = "gulimall-brands-logo";
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "3-2.jpg";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        String filePath= "C:\\Users\\iceri\\Documents\\3.jpg";

        try {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 创建PutObject请求。
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
