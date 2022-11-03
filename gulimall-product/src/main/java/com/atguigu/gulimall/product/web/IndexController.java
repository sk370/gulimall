package com.atguigu.gulimall.product.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className IndexController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/27 16:56
 */
@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    /**
     * 首页访问，获得一级分类数据【用于首页加载后直接显示】
     * @param model
     * @return
     */
    @GetMapping({"/", "index.html"})
    public String indexPage(Model model){
        // 1. 查询所有1级分类
        List<CategoryEntity> categoryEntityList = categoryService.getLevel1Categorys();
        model.addAttribute("categories", categoryEntityList);

        return "index";
    }

    /**
     * 查询出一级分类、二级分类和三级分类【用于鼠标hover时显示】
     * @param model
     * @return
     */
    @ResponseBody
    @GetMapping({"index/json/catalog.json"})
    public Map<String, List<Catelog2Vo>> getCatalogJson(Model model){
        // 1. 查询所有1级分类
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();

        return map;
    }

    /**
     * 简单服务性能测试
     * @return
     */
    @ResponseBody
    @GetMapping({"/hello"})
    public String hello(){

        return "hello";
    }

}
