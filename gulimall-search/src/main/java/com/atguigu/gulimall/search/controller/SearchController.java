package com.atguigu.gulimall.search.controller;

import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 前台页面的控制器方法——页面跳转及处理
 * @author zhuyuqi
 * @version v0.0.1
 * @className SearchController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/29 22:20
 */
@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    /**
     * 自动将页面提交过来的所有请求查询参数封装为指定的对象
     * @param param
     * @param model
     * @return
     */
    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model){
        // 1. 根据传递过来的页面查询参数，去ES中检索商品
        SearchResult result = mallSearchService.serach(param);

        model.addAttribute("result", result);//封装查询结果
        return "list";
    }
}
