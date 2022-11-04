package com.atguigu.common.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 登录成功返回给前端页面的数据
 * @author zhuyuqi
 * @version v0.0.1
 * @className MemberRespVo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/03 22:09
 */
@Data
public class MemberRespVo {
    private Long id;
    /**
     * 会员等级id
     */
    private Long levelId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String header;
    /**
     * 性别
     */
    private Integer gender;
    /**
     * 生日
     */
    private Date birth;
    /**
     * 所在城市
     */
    private String city;
    /**
     * 职业
     */
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 用户来源
     */
    private Integer sourceType;
    /**
     * 积分
     */
    private Integer integration;
    /**
     * 成长值
     */
    private Integer growth;
}
