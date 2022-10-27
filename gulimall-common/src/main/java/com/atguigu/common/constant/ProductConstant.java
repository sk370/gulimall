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
    /**
     * 商品属性：规格参数或销售属性
     */
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

    /**
     * 商品状态：新建、上架、下架
     */
    public enum StatusEnum{
        NEW_PRODUCT(0, "新建"), SPU_UP(1, "商品上架"), SPU_DOWN(2, "商品下架");
        private int code;
        private String msg;
        StatusEnum(int code, String msg){
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
