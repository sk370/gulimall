package com.atguigu.common.valid;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zhuyuqi
 * @version v0.0.1
 * @className ListValueConstraintValidator
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/10/21 16:58
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue, Integer> {//泛型1为指定的注解，泛型2为可修饰的属性类型
    private Set<Integer> set = new HashSet<>();

    /**
     * 初始化方法
     * @param constraintAnnotation
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        // TODO 对vals进行非空判断后遍历
        for (int val : vals){
            set.add(val);
        }
    }

    /**
     * 判断是否校验成功
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return set.contains(value);
    }
}
