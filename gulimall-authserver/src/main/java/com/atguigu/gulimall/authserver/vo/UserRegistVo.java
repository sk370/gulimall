package com.atguigu.gulimall.authserver.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 *
 * @author zhuyuqi
 * @version v0.0.1
 * @className UserRegistVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 19:36
 */
@Data
public class UserRegistVo {
    @NotEmpty(message = "用户名必须提交")
    @Length(min = 6, max = 18, message = "用户名必须在6-18位")
    private String userName;

    @NotEmpty(message = "密码必须提交")
    @Length(min = 6, max = 18, message = "密码必须是6-18位")
    private String password;

    @NotEmpty(message = "电话号必须提交")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$",message = "手机号格式不正确")
    private String phone;

    private String code;
}
