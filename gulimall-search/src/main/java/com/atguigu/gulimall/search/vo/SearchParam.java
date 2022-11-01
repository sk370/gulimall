package com.atguigu.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * @author zhuyuqi
 * @version v0.0.1
 * @className SearchParam
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/30 08:53
 */
@Data
public class SearchParam {
//    检索条件：
//      ● 全文检索：skuTitle-》keyword
//      ● 排序：saleCount（销量）、hotScore（热度分）、skuPrice（价格）
//      ● 过滤：hasStock、skuPrice区间、brandId、catalog3Id、attrs
//      ● 聚合：attrs
//    完整查询参数示例：keyword=小米&sort=saleCount_desc/asc&hasStock=0/1&skuPrice=400_1900&brandId=1&catalog3Id=1&attrs=1_3G:4G:5G&attrs=2_骁龙845&attrs=4_高清屏

    private String keyword;//搜索关键字
    private Integer catalog3Id;//三级分类id
    private String order;//排序条件
    /**
     * sort=saleCount_asc/desc  销量排序
     * sort=skuPrice_asc/desc   价格排序
     * sort=hotScore_asc/desc   热度排序
     */
//    private Integer hasStock = 1;//是否只显示有货[0无货，1有货]
    private Integer hasStock;//是否只显示有货[0无货，1有货]
    private String skuPrice;//价格区间查询
    private List<Long> brandId;//按照品牌查询，可以多选
    private List<String> attrs;//按照属性进行筛选，可以多选  attrs=1_3G:4G:5G 表示1号属性，值为3G、4G、5G
    private Integer pageNum = 1;//页码

    // 面包屑导航相关数据
    private String _queryString;//原生的所有查询条件——？后的内容
}
