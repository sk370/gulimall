package com.atguigu.gulimall.member.service;

import java.util.Map;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.vo.UserRegistVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 会员
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 自定义方法
     */
    void regist(UserRegistVo vo);

    /**
     * 自定义方法：使用异常机制处理是否唯一
     * @param phone
     */
    void checkPhoneUnique(String phone) throws PhoneExistException;

    /**
     * 自定义方法：使用异常机制处理是否唯一
     * @param username
     */
    void checeUserNameUnique(String username) throws UserNameExistException;//调用该接口时，可以手动选择是否try catch，否则只能等待发生
}

