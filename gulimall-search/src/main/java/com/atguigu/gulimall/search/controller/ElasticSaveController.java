package com.atguigu.gulimall.search.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.to.es.SkuESModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ProductSaveService;

import lombok.extern.slf4j.Slf4j;

/**
 * 保存检索信息到ES
 * @author zhuyuqi
 * @version v0.0.1
 * @className ElasticSaveController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 10:02
 */
@RestController
@RequestMapping("/search/save")
@Slf4j
public class ElasticSaveController {
    @Autowired
    ProductSaveService productSaveService;

    /**
     * 上架商品
     * @param skuESModels
     * @return
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuESModel> skuESModels) {
        boolean b = false;
        try {
            b = productSaveService.productStatusUp(skuESModels);//保存到ES
        } catch (IOException e) {
            log.error("ElasticSaveController商品上架错误{}", e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(!b){
            return R.ok();
        }else {
            log.error("ElasticSaveController商品上架有失败的");
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
    }
}
