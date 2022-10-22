package com.atguigu.common.exception;

/**
 * 错误码及错误信息
 * @author zhuyuqi
 * @version v2.0
 * @enumName BizCodeEnum
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/21 16:09
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败");
    private int code;
    private String msg;
    BizCodeEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
