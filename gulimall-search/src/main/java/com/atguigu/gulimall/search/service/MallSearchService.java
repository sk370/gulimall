package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

/**
 * @author zhuyuqi
 * @version v2.0
 * @interfaceName MallSearchService
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/30 08:58
 */
public interface MallSearchService {
    /**
     *
     * @param param 检索的所有参数
     * @return 检索的结果
     */
    SearchResult serach(SearchParam param);
}
