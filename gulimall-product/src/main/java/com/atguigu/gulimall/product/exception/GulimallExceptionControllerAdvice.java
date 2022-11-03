package com.atguigu.gulimall.product.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.utils.R;

import lombok.extern.slf4j.Slf4j;

/**
 * 对前端传过来的数据进行校验，校验不通过抛出异常，该类用于处理异常
 * @author zhuyuqi
 * @version v0.0.1
 * @className GulimallExceptionControllerAdvice
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/21 15:47
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.gulimall.product.controller")
public class GulimallExceptionControllerAdvice {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)//如果不知道具体到的异常类型，可以先写Exception，让程序运行一遍，看报错写的什么异常
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型{}",e.getMessage(), e.getClass());
        Map<String, String> map = new HashMap<>();//存放错误信息
        BindingResult bindingResult = e.getBindingResult();//校验结果
        bindingResult.getFieldErrors().forEach((item) ->{//item是通过forEach获取到的每一个错误结果
            String message = item.getDefaultMessage();//错误消息
            String field = item.getField();//发生错误的字段
            map.put(field, message);
        });
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data", map);
    }
    @ExceptionHandler(value = Throwable.class)//优先匹配精确异常，未匹配使用该方法处理异常
    public R handleException(Throwable e){
        log.error("错误", e);

        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }
}

