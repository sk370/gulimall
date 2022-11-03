package com.atguigu.gulimall.member.exception;

import com.atguigu.common.exception.BizCodeEnum;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className UserNameExistException
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 21:23
 */
public class UserNameExistException extends RuntimeException{
    public UserNameExistException() {
        super(BizCodeEnum.USER_EXIST_EXCEPTION.getMsg());
    }
}
