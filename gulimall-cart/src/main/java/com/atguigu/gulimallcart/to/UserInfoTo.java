package com.atguigu.gulimallcart.to;

import lombok.Data;

/**
 * 临时购物车的用户：临时保存和整合
 * @author zhuyuqi
 * @version v0.0.1
 * @className UserInfoTo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/05 14:09
 */
@Data
public class UserInfoTo {
    private Long userId;//保存登录用户的id
    private String userKey;//保存到cookie中，作为value
    private Boolean tempUser = false;//cookie中是否已经有临时用户
}
