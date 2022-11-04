package com.atguigu.gulimall.authserver.po;

import lombok.Data;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className WeiboAcctPo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/03 18:56
 */
@Data
public class WeiboAcctPo {
    private String access_token;//access_token
    private String remind_in;//remind_in
    private String uid;//uid
    private long expires_in;//expires_in
    private String isRealName;//isRealName
}
