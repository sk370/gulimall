package com.atguigu.common.constant;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className WareConstant
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/25 09:44
 */
public class WareConstant {
    public enum PurchaseStatus{
        CREATED(0, "新建"), ASSIGNED(1, "已分配"),RECEIVE(2, "已领取"), FINISH(3, "已完成"), HAS_ERROR(4, "有异常");
        private int code;
        private String msg;
        PurchaseStatus(int code, String msg){
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
    public enum DetailStatus{
        CREATED(0, "新建"), ASSIGNED(1, "已分配"),BUYING(2, "正在采购"), FINISH(3, "已完成"), HAS_ERROR(4, "采购失败");
        private int code;
        private String msg;
        DetailStatus(int code, String msg){
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
