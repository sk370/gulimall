package com.atguigu.gulimall.search.vo;

import com.atguigu.common.to.es.SkuESModel;
import lombok.Data;

import java.util.List;

/**
 * 查询后返回给页面的数据
 * @author zhuyuqi
 * @version v0.0.1
 * @className SearchResult
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/30 09:21
 */
@Data
public class SearchResult {
    private List<SkuESModel> products;//查询到的所有商品信息
    private Integer pageNum;//当前页码
    private Long total;//总记录数
    private Integer totalPages;//总页码
    private List<Integer> pageNavs;//导航页
    private List<BrandVo> brands;//当前查询获得的结果涉及到的所有品牌
    private List<AttrVo> attrs;//查询到的品牌涉及到的所有属性
    private List<CatalogVo> catalogs;//当前查询获得的结果涉及到的所有的分类

    private List<NavVo> navs;//面包屑导航

    /**
     * 面包屑导航
     */
    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }

    /**
     * 查询到的品牌
     */
    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    /**
     * 当前查询获得的结果涉及到的所有的分类
     */
    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    /**
     * 查询到的品牌涉及到的所有属性
     */
    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }
}
