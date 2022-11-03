package com.atguigu.gulimall.member.vo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

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
    private String userName;

    private String password;

    private String phone;
}
