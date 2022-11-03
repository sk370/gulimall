package com.atguigu.gulimall.ware.service;

import java.util.List;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:12:48
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     * @param id
     * @return
     */
    List<PurchaseDetailEntity> listDetailByPurchaseId(Long id);
}

