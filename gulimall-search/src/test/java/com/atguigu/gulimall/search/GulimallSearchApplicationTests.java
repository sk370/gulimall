package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void contextLoads() {
        System.out.println(restHighLevelClient);
    }

    @Data
    static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    /**
     * 测试复杂检索：
     * 搜索 address 中包含 mill 的所有人的年龄分布以及平均年龄。
     */
    @Test
    public void testSearch(){
        // 1. 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");//指定索引
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);//指定DSL器
        // 1.1 构造检索条件
//        searchSourceBuilder.query();
//        searchSourceBuilder.from();
//        searchSourceBuilder.size();
//        searchSourceBuilder.aggregation();
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        // 1.2 按照年龄的值分布进行聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        searchSourceBuilder.aggregation(ageAgg);
        // 1.3 计算平均薪资
        TermsAggregationBuilder balanceAvg = AggregationBuilders.terms("balanceAvg").field("balance");
        searchSourceBuilder.aggregation(balanceAvg);

        System.out.println("检索条件" + searchSourceBuilder.toString());
        searchRequest.source(searchSourceBuilder);

        // 2. 执行检索
        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. 分析查询结果
//        System.out.println("检索结果" + search.toString());
        //JSON.parseObject(search.toString(), Map.class);//使用Java的方式解析json
        // 3.1 使用es的api处理——获取命中记录集
        SearchHits hits = search.getHits();
        SearchHit[] hitsHits = hits.getHits();
        for (SearchHit hit : hitsHits){
//            hit.getIndex(); hit.getType();hit.getId();
            String string = hit.getSourceAsString();//获取hit结果，转为json字符串
            Account account = JSON.parseObject(string, Account.class);//利用fastjson工具转换为Account对象
            System.out.println("account" + account);

        }
        // 3.2 获取本次检索到的聚合数据
        Aggregations aggregations = search.getAggregations();
//        for(Aggregation aggregation : aggregations.asList()){
//            System.out.println("当前聚合" + aggregation.getName());
//        }
        Terms ageAggResult = aggregations.get("ageAgg");
        for(Terms.Bucket bucket : ageAggResult.getBuckets()){
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄" + keyAsString + "====>" + bucket.getDocCount());
        }
//        Avg balanceAvgResult = aggregations.get("balanceAvg");
//        System.out.println("平均薪资" + balanceAvgResult.getValue());
    }

    /**
     * 测试给es中存储数据
     */
    @Test
    public void testSave(){
        IndexRequest indexRequest = new IndexRequest("user");//设置索引
        indexRequest.id("1");//设置id
//        indexRequest.source("username","zhangsan", "age", "18", "gender", "男");//存储数据：方式一
        //存储数据：方式二
        User user = new User();
        user.setUsername("zhangsan");
        user.setGender("男");
        user.setAge(18);
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);

        //执行保存操作
        try {
            IndexResponse index = restHighLevelClient.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            System.out.println(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    class User{
        private String username;
        private String gender;
        private Integer age;
    }

}
