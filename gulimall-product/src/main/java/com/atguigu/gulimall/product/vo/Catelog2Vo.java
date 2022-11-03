package com.atguigu.gulimall.product.vo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 前台页面的二级菜单对象：二级分类vo
 * @author zhuyuqi
 * @version v0.0.1
 * @className Catelog2Vo
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 17:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Catelog2Vo {
    private String catalog1Id;//1级父分类id
    private List<Catelog3Vo> catalog3List;//3级分类id
    private String id;//2级分类id
    private String name;//2级分类名称

    /**
     * 三级分类vo
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Catelog3Vo{

        private String catalog2Id;//2级父分类id
        private String id;//3级分类id
        private String name;//3级分类名称
    }
}
