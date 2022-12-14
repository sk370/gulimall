package com.atguigu.common.exception;

/**
 * 错误码及错误信息
 * 10 通用
 *   001 参数格式校验
 *   002 验证码获取频率过高
 * 11 商品
 * 12 订单
 * 13 购物车
 * 14 物流
 * @author zhuyuqi
 * @version v2.0
 * @enumName BizCodeEnum
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/21 16:09
 */
public enum BizCodeEnum {
    UNKNOW_EXCEPTION(10000, "系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    TOO_MANY_REQUEST(10002,"请求流量过大"),
    VALID_SMS_CODE_EXCEPTION(10002,"验证码获取频率过高"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
    USER_EXIST_EXCEPTION(15001, "用户名已占用"),
    PHONE_EXIST_EXCEPTION(15002, "该号码已注册"),
    LOGINACCT_PASSWORD_INVLAID_EXCEPTION(15003, "账户名或密码错误"),
    NO_STOCK_EXCEPTION(21000, "商品库存不足");
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
