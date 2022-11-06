package com.atguigu.gulimallcart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimallcart.to.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.UUID;

/**
 * 在执行目标方法之前，判断用户的的状态，并封装传递给目标请求
 * @author zhuyuqi
 * @version v0.0.1
 * @className CartInterceptor
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/05 14:04
 */
//@Component//不需要这个注解，gulimallwebconfig中new创建了
public class CartInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();
    /**
     * 执行目标方法之前拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo attribute = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);//获取已登录用户的账号信息
        if(attribute != null){//已经登录用户
            userInfoTo.setUserId(attribute.getId());
        }

        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if(Objects.equals(name, CartConstant.TEMP_USER_COOKIE)){//获取指定name的cookie，如果没有临时用户则没有该cookie
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }

        // 如果cookie中没值 或者没有cookie，则说明没有临时用户，创建一个临时用户——cookie的value和userInfoTo的value都是uuid
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
        }
        System.out.println(userInfoTo.getUserKey() + ":userInfoTo~~~~~~~~~~~~~~~~~~");

        threadLocal.set(userInfoTo);
        return true;
    }

    /**
     * 执行目标方法之后；设置cookie信息（创建一个临时用户）
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if(!userInfoTo.getTempUser()) {
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIME_OUT);
            response.addCookie(cookie);//将cookie发送给浏览器
        }
    }
}
