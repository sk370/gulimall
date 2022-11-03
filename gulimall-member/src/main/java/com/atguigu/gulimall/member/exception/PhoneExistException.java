package com.atguigu.gulimall.member.exception;

import com.atguigu.common.exception.BizCodeEnum;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className PhoneExistException
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 21:23
 */
public class PhoneExistException extends RuntimeException{
    public PhoneExistException() {
        super(BizCodeEnum.PHONE_EXIST_EXCEPTION.getMsg());
    }
}
