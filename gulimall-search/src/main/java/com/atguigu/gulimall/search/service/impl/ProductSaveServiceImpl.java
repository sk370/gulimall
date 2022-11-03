package com.atguigu.gulimall.search.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.to.es.SkuESModel;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.ESConstant;
import com.atguigu.gulimall.search.service.ProductSaveService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className ProductSaveServiceImpl
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 10:07
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 保存到ES
     * @param skuESModels
     */
    @Override
    public boolean productStatusUp(List<SkuESModel> skuESModels) throws IOException {
        // 1. 给ES中建立索引product，建立好mapping映射关系（在kibana中完成）

        // 2. 给ES中保存这些数据
//        public final BulkResponse bulk(BulkRequest bulkRequest, RequestOptions options)
        BulkRequest bulkRequest = new BulkRequest();//要保存的数据
        skuESModels.forEach(model ->{
            IndexRequest indexRequest = new IndexRequest(ESConstant.PRODUCT_INDEX);//构造保存请求并指定索引
            indexRequest.id(model.getSkuId().toString());//保存ID
            indexRequest.source(JSON.toJSONString(model), XContentType.JSON);//保存数据并指定类型
            bulkRequest.add(indexRequest);
        });
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);//批量保存
        //TODO 是否批量上架错误【完成】
        boolean b = bulk.hasFailures();
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            return item.getId();
        }).collect(Collectors.toList());//上架完成的商品id
        log.info("上架完成的商品id：{},返回数据{}", collect, bulk.toString());

        return b;
    }
}
