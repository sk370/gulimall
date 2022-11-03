package com.atguigu.gulimall.member.service.impl;

import java.util.Date;
import java.util.Map;

import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();//获取默认会员等级
        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setCreateTime(new Date());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setStatus(1);
        // 1. 检查用户名与手机号是否唯一
        checkPhoneUnique(vo.getPhone());//有异常直接终止程序
        checeUserNameUnique(vo.getUserName());//有异常直接终止程序
        // 2. 密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);

        baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(count > 0) throw new PhoneExistException();
    }

    @Override
    public void checeUserNameUnique(String username) throws UserNameExistException{
        Integer count = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if(count > 0) throw new UserNameExistException();
    }

}