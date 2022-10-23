package com.atguigu.common.constant;

import org.w3c.dom.Attr;

/**
 * 与商品相关的常量
 * @author zhuyuqi
 * @version v0.0.1
 * @className ProductConstant
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/23 23:28
 */
public class ProductConstant {
    public enum AttrEnum{
        ATTR_TYPE_BASE(1, "基本属性"), ATTR_TYPE_SALE(0, "销售属性");
        private int code;
        private String msg;
        AttrEnum(int code, String msg){
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
}
