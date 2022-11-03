package com.atguigu.gulimall.product;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.SkuSaleAttrValueDao;
import com.atguigu.gulimall.product.service.BrandService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.SkuItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SpuItemAttrGroupVo;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)//需要手动添加这个，不然注入不了组件
public class GulimallProductApplicationTests {
    @Autowired
    BrandService brandService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public void testSql1(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(117L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }
    @Test
    public void testSql2(){
        List<SkuItemSaleAttrVo> saleAttrVos = skuSaleAttrValueDao.getSaleAttrsBySpuId(7L);
        System.out.println(saleAttrVos);
    }

    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(231L);
        log.info("完整路径:{}", Arrays.asList(catelogPath));
    }

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

    /**
     * 手动引入：测试阿里云OOS文件上传
     */
//    @Test
//    public void testUpload(){
//        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
//        String endpoint = "https://oss-cn-chengdu.aliyuncs.com";
//        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5t65mPsBz6PhbREpcsAk";
//        String accessKeySecret = "hWKPQUYZKm0uwy03Cw2Aizk3aVHlol";
//        // 填写Bucket名称，例如examplebucket。
//        String bucketName = "gulimall-brands-logo";
//        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
//        String objectName = "3.jpg";
//        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
//        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        String filePath= "C:\\Users\\iceri\\Documents\\3.jpg";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        try {
//            InputStream inputStream = null;
//            try {
//                inputStream = new FileInputStream(filePath);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            // 创建PutObject请求。
//            ossClient.putObject(bucketName, objectName, inputStream);
//        } catch (OSSException oe) {
//            System.out.println("Caught an OSSException, which means your request made it to OSS, "
//                    + "but was rejected with an error response for some reason.");
//            System.out.println("Error Message:" + oe.getErrorMessage());
//            System.out.println("Error Code:" + oe.getErrorCode());
//            System.out.println("Request ID:" + oe.getRequestId());
//            System.out.println("Host ID:" + oe.getHostId());
//        } catch (ClientException ce) {
//            System.out.println("Caught an ClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with OSS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message:" + ce.getMessage());
//        } finally {
//            if (ossClient != null) {
//                ossClient.shutdown();
//            }
//        }
//    }
}
