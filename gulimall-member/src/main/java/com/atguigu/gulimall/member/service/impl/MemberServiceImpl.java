package com.atguigu.gulimall.member.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.po.WeiboAcctPo;
import com.atguigu.gulimall.member.vo.UserLoginVo;
import com.atguigu.gulimall.member.vo.UserRegistVo;
import org.apache.http.entity.BasicHttpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {
    @Autowired
    MemberLevelDao memberLevelDao;
    @Autowired
    RestTemplate restTemplate;

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

    @Override
    public MemberEntity login(UserLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(memberEntity == null){
            // 可以采用异常机制，抛出特定异常——同注册【这里犯懒没写】
            return null;
        } else {
            String passwordDB = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            boolean matches = passwordEncoder.matches(password, passwordDB);
            if(matches){
                return memberEntity;
            }
        }
        return null;
    }

    @Override
    public MemberEntity login(WeiboAcctPo po) {
        // 具有登录和注册两个功能
        String uid = po.getUid();
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity != null){
            // 当前用户已经注册，可以直接登录。
            MemberEntity entity = new MemberEntity();
            entity.setId(memberEntity.getId());
            entity.setAccessToken(po.getAccess_token());
            entity.setExpiresIn(po.getExpires_in());
            baseMapper.updateById(entity);
            memberEntity.setAccessToken(entity.getAccessToken());
            memberEntity.setExpiresIn(entity.getExpiresIn());
            return memberEntity;
        } else {//注册用户
            MemberEntity entity = new MemberEntity();
            MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();//获取默认会员等级
            entity.setLevelId(memberLevelEntity.getId());
            entity.setCreateTime(new Date());
            entity.setAccessToken(po.getAccess_token());
            entity.setExpiresIn(po.getExpires_in());
            entity.setSocialUid(po.getUid());
            entity.setStatus(1);
            try {//远程请求失败，不影响正常登录

                // 模拟表单发送post请求
                // 请求头设置,x-www-form-urlencoded格式的数据
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                //提交参数设置
                Map<String, String> map = new HashMap<>();
                map.put("access_token", po.getAccess_token());
                map.put("uid", po.getUid());

                // get，并打印结果
                String url = "https://api.weibo.com/2/users/show.json?access_token={access_token}&uid={uid}";
                String result = restTemplate.getForObject(url,String.class,map);
                System.out.println(result + "~~~~~~~~~~~~~~~~~~~~~~");
                if(result != null){// 判断返回结果
                    JSONObject parseObject = JSON.parseObject(result);
                    String gender = parseObject.getString("gender");
                    String nickName = parseObject.getString("nickName");
                    entity.setNickname(nickName);
                    entity.setGender(Objects.equals("m",gender)?1:0);
                }
            } catch (RestClientException e) {
                e.printStackTrace();
            }
            baseMapper.insert(entity);
            return entity;
        }
    }

}