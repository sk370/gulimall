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
        MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();//????????????????????????
        memberEntity.setLevelId(memberLevelEntity.getId());
        memberEntity.setCreateTime(new Date());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setStatus(1);
        // 1. ???????????????????????????????????????
        checkPhoneUnique(vo.getPhone());//???????????????????????????
        checeUserNameUnique(vo.getUserName());//???????????????????????????
        // 2. ????????????
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
            // ????????????????????????????????????????????????????????????????????????????????????
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
        // ?????????????????????????????????
        String uid = po.getUid();
        MemberEntity memberEntity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberEntity != null){
            // ????????????????????????????????????????????????
            MemberEntity entity = new MemberEntity();
            entity.setId(memberEntity.getId());
            entity.setAccessToken(po.getAccess_token());
            entity.setExpiresIn(po.getExpires_in());
            baseMapper.updateById(entity);
            memberEntity.setAccessToken(entity.getAccessToken());
            memberEntity.setExpiresIn(entity.getExpiresIn());
            return memberEntity;
        } else {//????????????
            MemberEntity entity = new MemberEntity();
            MemberLevelEntity memberLevelEntity = memberLevelDao.getDefaultLevel();//????????????????????????
            entity.setLevelId(memberLevelEntity.getId());
            entity.setCreateTime(new Date());
            entity.setAccessToken(po.getAccess_token());
            entity.setExpiresIn(po.getExpires_in());
            entity.setSocialUid(po.getUid());
            entity.setStatus(1);
            try {//??????????????????????????????????????????

                // ??????????????????post??????
                // ???????????????,x-www-form-urlencoded???????????????
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                //??????????????????
                Map<String, String> map = new HashMap<>();
                map.put("access_token", po.getAccess_token());
                map.put("uid", po.getUid());

                // get??????????????????
                String url = "https://api.weibo.com/2/users/show.json?access_token={access_token}&uid={uid}";
                String result = restTemplate.getForObject(url,String.class,map);
                System.out.println(result + "~~~~~~~~~~~~~~~~~~~~~~");
                if(result != null){// ??????????????????
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