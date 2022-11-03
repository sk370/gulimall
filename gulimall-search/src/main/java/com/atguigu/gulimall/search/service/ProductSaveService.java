package com.atguigu.gulimall.search.service;

import java.io.IOException;
import java.util.List;

import com.atguigu.common.to.es.SkuESModel;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className ProductSaveService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 10:06
 */
public interface ProductSaveService {
    boolean productStatusUp(List<SkuESModel> skuESModels) throws IOException;
}
