package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class GulimallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Test
    public void contextLoads() {
//        BrandEntity brandEntity = new BrandEntity();
        //1. 测试添加
//        brandEntity.setName("淘宝");
//        brandService.save(brandEntity);
//        System.out.println("保存成功");
        //2. 测试修改——给淘宝添加描述
//        brandEntity.setBrandId(6L);
//        brandEntity.setDescript("电商网站");
//        brandService.updateById(brandEntity);
        //3. 测试查询
//        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 6));
//        list.forEach((item) -> System.out.print(item));
    }

}
