package com.atguigu.gulimall.search.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuESModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.ESConstant;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.BrandVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className MallSearchServiceImpl
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/30 08:58
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult serach(SearchParam param) {
        SearchResult searchResult = new SearchResult();
        // 1.动态构建出需要查询的dsl语句
        // 1.1 准备检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //1.2 执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            // 1.3 分析响应数据，封装成目标格式
            searchResult = buildSearchResult(response, param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 构建返回结果
     * @param response
     * @param param
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
//        System.out.println(response);
        SearchResult searchResult = new SearchResult();
        // 1. 获取所有商品
        SearchHits hits = response.getHits();
        List<SkuESModel> products = new ArrayList<>();
//        List<SkuESModel> products = searchResult.getProducts();//这样写 products.add(esModel); 会有空指针异常
        if(hits != null && hits.getHits().length > 0){
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();//json格式
                SkuESModel esModel = JSON.parseObject(sourceAsString, SkuESModel.class);
                // 准备高亮
                // if(!StringUtils.isEmpty(param.getKeyword())) {//按照传参判断是否设置高亮。由于封装检索条件时按照改规则封装了，所以肯定有。但是我觉得应该按照检索结果判断

                if(!hit.getHighlightFields().isEmpty()){// 个人按照响应判断
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String title = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(title);

                }
                products.add(esModel);
            }
        }
        searchResult.setProducts(products);

        // 2. 获取分页信息
        Long totalHits = response.getHits().getTotalHits().value;//总记录数
        int i = totalHits.intValue();
        Integer totalPages = i % ESConstant.PRODUCT_PAGESIZE == 0 ? i / ESConstant.PRODUCT_PAGESIZE : i / ESConstant.PRODUCT_PAGESIZE + 1;
        List<Integer> pageNavs = new ArrayList<>();
        for (int j = 1; j <= totalPages; j++) {
            pageNavs.add(j);
        }

        searchResult.setTotal(totalHits);
        searchResult.setTotalPages(totalPages);
        searchResult.setPageNavs(pageNavs);
        searchResult.setPageNum(param.getPageNum());

        // 3. 获取聚合信息
        // 3.1 获取分类聚合信息
        List<SearchResult.CatalogVo> catalogs = new ArrayList<>();
//        List<SearchResult.CatalogVo> catalogs = searchResult.getCatalogs();
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            // 3.1.1 设置分类id
            Long key = (Long) bucket.getKeyAsNumber();
            // 3.1.2 设置分类名
            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();

            catalogVo.setCatalogName(catalogName);
            catalogVo.setCatalogId(key);
            catalogs.add(catalogVo);
        }
        searchResult.setCatalogs(catalogs);
        // 3.2 获取品牌聚合信息
        List<SearchResult.BrandVo> brands = new ArrayList<>();
//        List<SearchResult.BrandVo> brands = searchResult.getBrands();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            // 3.2.1 设置品牌id
            Long key = (Long) bucket.getKeyAsNumber();
            // 3.2.2 设置品牌图片
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String imgUrl = brand_img_agg.getBuckets().get(0).getKeyAsString();
            // 3.2.3 设置品牌名
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_img_agg");
            String name = brand_name_agg.getBuckets().get(0).getKeyAsString();

            brandVo.setBrandId(key);
            brandVo.setBrandImg(imgUrl);
            brandVo.setBrandImg(name);
            brands.add(brandVo);
        }
        searchResult.setBrands(brands);
        // 3.3 获取属性聚合
        List<SearchResult.AttrVo> attrs = new ArrayList<>();
//        List<SearchResult.AttrVo> attrs = searchResult.getAttrs();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        // 3.3.1 属性id聚合
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            // 3.3.1.1 属性id
            Long key = (Long) bucket.getKeyAsNumber();
            // 3.3.1.2 属性名
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            // 3.3.1.3 属性值
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<? extends Terms.Bucket> buckets = attr_value_agg.getBuckets();
            List<String> collect = buckets.stream().map(item -> {
                return item.getKeyAsString();
            }).collect(Collectors.toList());

            attrVo.setAttrId(key);
            attrVo.setAttrName(attrName);
            attrVo.setAttrValue(collect);

            attrs.add(attrVo);
        }
        searchResult.setAttrs(attrs);

        // 4. 面包屑导航
        if(param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {//attr为1_全网通
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //attrs=1_4寸:5寸&attrs=2_16G:18G
                String[] s = attr.split("_");//s[0]属性id s[1]属性值
                navVo.setNavValue(s[1]);
                R info = productFeignService.info(Long.parseLong(s[0]));//远程查询属性
                searchResult.getAttrIds().add(Long.parseLong(s[0]));
                if (info.getCode() == 0) {
                    AttrResponseVo data = info.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    String attrName = data.getAttrName();
                    navVo.setNavName(attrName);
                } else {
                    navVo.setNavName(s[0]);
                }
                String replace = replaceQueryString(param, attr, "attrs");
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(collect);
        }

        // 5. 分类及属性的面包屑导航
        // 5.1 品牌的面包屑导航
        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            List<SearchResult.NavVo> navs = searchResult.getNavs();//这里设置了在设定SearchResult.NavVo时，设置了List<NavVo> navs = new ArrayList<>();，主要是防止该处为null
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            R info = productFeignService.info(param.getBrandId());//远程方法调用
            if(info.getCode() == 0){
                List<BrandVo> brand = info.getData("brand", new TypeReference<List<BrandVo>>() {});
                StringBuffer stringBuffer = new StringBuffer();
                String replace = "";
                for (BrandVo brandVo : brand) {
                    stringBuffer.append(brandVo.getName() + ";");
                    replace = replaceQueryString(param, brandVo.getBrandId() + "", "brandId");
                }
                navVo.setNavValue(stringBuffer.toString());
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
            }
            navs.add(navVo);
        }
        // 5.2 TODO 分类的面包屑导航 (不需要导航取消——不需要拼接链接）



//        System.out.println(searchResult);
        return searchResult;
    }

    /**
     * 封装成一个导航链接：即每一个NavVo的link都是一个可点击的链接，且链接不包含当前参数条件。最后按照浏览器的格式对链接进行编码
     * @param param
     * @param value
     * @param key
     * @return
     */
    private String replaceQueryString(SearchParam param, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");//如果属性值带空格，会被encode成+；前端空格用%20表示，所以要将+编程空格，即%20替换
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String replace = null;
        if(param.get_queryString().contains("&" + key + "=" + encode)){//判断当前参数是第一个参数还是第n个参数
            replace = param.get_queryString().replace("&" + key + "=" + encode, "");

        }else {
            replace = param.get_queryString().replace(key + "=" + encode, "");
        }

        return replace;
    }

    /**
     * 构建检索条件
     * @param param
     * @return 检索请求
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();//用于构建dsl语句

        // 1. 查询：模糊匹配、过滤（属性、分类、品牌、价格区间、库存）
        // 1.1 构建bool_query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        // 1.1.1 构建must
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));//模糊匹配
        }
        // 1.1.2 构建filter
        // 1.1.2.1 三级分类id查询
        if(param.getCatalog3Id() != null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));//三级分类id
        }
        // 1.1.2.2 库存查询
//        boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));//库存
        if(param.getHasStock() != null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        // 1.1.2.3 品牌id查询
        if(param.getBrandId() != null && param.getBrandId().size() > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));//品牌id
        }
        // 1.1.2.4 属性查询
        if(param.getAttrs() != null && param.getAttrs().size() > 0){//属性
            //attrs=1_4寸:5寸&attrs=2_16G:18G
            for (String attrStr : param.getAttrs()) {
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                String[] s = attrStr.split("_");
                String attrId = s[0];
                String[] attrValue = s[1].split(":");
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValue));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        // 1.1.2.5 价格区间查询
        if(!StringUtils.isEmpty(param.getSkuPrice())){//价格区间传参格式为x_y
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if(s.length == 2){//0-500这种价格区间
                rangeQuery.gte(Double.parseDouble(s[0])).lte(Double.parseDouble(s[1]));
            }else if(s.length == 1){
                if (param.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(Double.parseDouble(s[0]));
                }
                if(param.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(Double.parseDouble(s[0]));
                }
            }
            boolQuery.filter(rangeQuery);
        }
        // 1.1.3 将查询条件放入dsl语句
        sourceBuilder.query(boolQuery);

        // 2. 排序；分页；高亮
        // 2.1 排序：sort=hostScore_asc/desc【单属性排序】
        if(!StringUtils.isEmpty(param.getOrder())){
            String order = param.getOrder();
            String[] s = order.split("_");
            SortOrder orderRegular = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0], orderRegular);
        }
        // 2.2 分页
        sourceBuilder.from((param.getPageNum() - 1) * ESConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(ESConstant.PRODUCT_PAGESIZE);
        // 2.3 高亮
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }

        // 3. 聚合分析
         // 3.1 品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(20);//显示20条数据
        // 3.1.1 品牌聚合的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);
        // 3.2 商品分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(1);
        // 3.2.1 商品分类子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(20));
        sourceBuilder.aggregation(catalog_agg);
        // 3.3 属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 3.3.1 属性聚合子聚合
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 3.3.1.1 属性聚合子聚合的子聚合
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attr_agg);

//        System.out.println("构建的dsl语句：" + sourceBuilder.toString() + "类型为"+ sourceBuilder.getClass());
        SearchRequest searchRequest = new SearchRequest(new String[]{ESConstant.PRODUCT_INDEX}, sourceBuilder);//构造检索请求

        return searchRequest;
    }
}
