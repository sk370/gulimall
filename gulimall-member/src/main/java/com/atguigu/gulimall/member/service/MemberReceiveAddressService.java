package com.atguigu.gulimall.member.service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.member.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * 会员收货地址
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

