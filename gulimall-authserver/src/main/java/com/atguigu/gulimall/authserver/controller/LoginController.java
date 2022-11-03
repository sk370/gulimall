package com.atguigu.gulimall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.authserver.feign.MemeberFeignService;
import com.atguigu.gulimall.authserver.feign.ThirdPartyFeignService;
import com.atguigu.gulimall.authserver.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录及注册请求处理
 * @author zhuyuqi
 * @version v0.0.1
 * @className LoginController
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/02 12:04
 */
@Controller
public class LoginController {
    @Autowired
    private ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    MemeberFeignService memeberFeignService;

//    @GetMapping({"/","/login.html"})
//    public String loginPage(){
//        return "login";
//    }
//
//    @GetMapping("/register.html")
//    public String regPage(){
//        return "register";
//    }

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam("phone") String phone){
        // 1. TODO 接口防刷（防止刷新页面发送重复请求）
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60 * 1000) {
                return R.error(BizCodeEnum.VALID_SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.VALID_SMS_CODE_EXCEPTION.getMsg());
            }
        }
        // 2. 生成验证码并缓存
        String code = UUID.randomUUID().toString().substring(0, 5);
        System.out.println(code+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        redisCode = code + "_" + System.currentTimeMillis();
        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, redisCode, 300, TimeUnit.SECONDS);

        // 3.向注册用户发送验证码
        thirdPartyFeignService.sendCode(phone, code);
        return R.ok();
    }

    /**
     * 注册
     * @param vo
     * @param result 接收数据校验的结果
     * @param model 重定向的视图
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes model){
        // 1. 参数校验
        Map<String, String> errors = new HashMap<>();
        if(result.hasErrors()){
            errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField,  FieldError::getDefaultMessage));
            model.addFlashAttribute("errors", errors);
//            return "reg";//被thymeleaf处理
//            return "forward:/reg.html";//被springmvc处理，二者结果一样，但是要求本处理器方法和后续的处理器方法处理的请求必须一致
//            return "redirect:/register.html";//定位到当前主机ip的/register.html——不正确
            return "redirect:http://auth.gulimall.com/register.html";//重定向解决刷新表单二次提交数据问题，但是Model视图的数据会丢失，需要使用RedirectAttributes。但是该重定向是使用session的形式进行数据转移，分布式下存在问题。
        }

        // 2. 验证码校验
        String phone = vo.getPhone();
        String code = vo.getCode();
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(!StringUtils.isEmpty(redisCode)){
            String s = redisCode.split("_")[0];
            if(Objects.equals(code, s)){
                // 3. 删除验证码
                stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);//删除验证码；令牌机制
                // 4. 注册：调用gulimall-member服务
                R regist = memeberFeignService.regist(vo);
                if(regist.getCode() == 0){
//                    return "redirect:/login.html";//被GulimallWebConfig处理【不能正确定位】
                    return "redirect:http://auth.gulimall.com/login.html";//
                }else {
                    String data = regist.getData("msg",new TypeReference<String>(){});
                    errors.put("msg", data);
                    model.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/register.html";
                }
            } else {
                errors.put("code", "验证码验证失败");
                model.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/register.html";
            }
        }else {
            errors.put("code", "验证码过期或失效");
            model.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/register.html";
        }
    }

    @PostMapping("/login")
}
