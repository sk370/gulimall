package com.atguigu.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimall.member.exception.PhoneExistException;
import com.atguigu.gulimall.member.exception.UserNameExistException;
import com.atguigu.gulimall.member.po.WeiboAcctPo;
import com.atguigu.gulimall.member.vo.UserLoginVo;
import com.atguigu.gulimall.member.vo.UserRegistVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.feign.CouponFeignService;
import com.atguigu.gulimall.member.service.MemberService;



/**
 * 会员
 *
 * @author zhuyuqi
 * @email icerivericeriver@hotmail.com
 * @date 2022-07-30 11:11:31
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    @Autowired
    CouponFeignService couponFeignService;


    /**
     * 用户第三方账号登录
     * @param po
     * @return
     */
    @PostMapping("oauth2/login")
    public R oauthLogin(@RequestBody WeiboAcctPo po){
        System.out.println(po);
        MemberEntity entity = memberService.login(po);
        if(entity!=null){
            MemberRespVo memberEntity = new MemberRespVo();
            BeanUtils.copyProperties(entity, memberEntity);
            return R.ok().put("msg", memberEntity);
        }else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVLAID_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVLAID_EXCEPTION.getMsg());
        }
    }

    /**
     * 用户登录
     * @param vo
     * @return
     */
    @PostMapping("/login")
    public R login(@RequestBody UserLoginVo vo){
        MemberEntity entity = memberService.login(vo);
        if(entity!=null){
            MemberRespVo memberEntity = new MemberRespVo();
            BeanUtils.copyProperties(entity, memberEntity);
            return R.ok().put("msg", memberEntity);
        }else {
            return R.error(BizCodeEnum.LOGINACCT_PASSWORD_INVLAID_EXCEPTION.getCode(), BizCodeEnum.LOGINACCT_PASSWORD_INVLAID_EXCEPTION.getMsg());
        }
    }

    /**
     * 用户注册
     * @return
     */
    @PostMapping("/regist")
    public R regist(@RequestBody UserRegistVo vo){
        try {
            memberService.regist(vo);
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnum.USER_EXIST_EXCEPTION.getCode(), e.getMessage());
        } catch (PhoneExistException e){
            return R.error(BizCodeEnum.PHONE_EXIST_EXCEPTION.getCode(), e.getMessage());
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);
        return R.ok().put("page", page);
    }
    /**
     * 1. 测试注册中心的服务相互调用：返回指定会员的优惠券
     * @return
     */
    @RequestMapping("/coupons")
    public R test(){
        //创建测试优惠券
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R membercoupons = couponFeignService.membercoupons();
        return R.ok().put("coupons", memberEntity).put("coupons:",membercoupons.get("coupons"));
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
