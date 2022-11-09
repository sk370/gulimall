package com.atguigu.gulimall.ware.service;

import java.math.BigDecimal;
import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareInfoEntity;
import com.atguigu.gulimall.ware.vo.FareRespvO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 仓库信息
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 12:12:48
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     * @param addrId
     * @return
     */
    FareRespvO getFare(Long addrId);
}

