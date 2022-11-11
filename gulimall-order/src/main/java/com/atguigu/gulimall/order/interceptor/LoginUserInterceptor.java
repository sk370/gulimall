package com.atguigu.gulimall.order.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截请求（登录状态检查）
 * @author zhuyuqi
 * @version v0.0.1
 * @className LoginUserInterceptor
 * @description https://developer.aliyun.com/profile/sagwrxp2ua66w
 * @date 2022/11/07 21:28
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean match1 = pathMatcher.match("/order/order/status/**", uri);//判断是不是ware服务远程调用过来的（这个不用登陆检查）
        boolean match2 = pathMatcher.match("/payed/notify", uri);//判断是不是给支付宝回调的（这个不用登陆检查）
        if(match1||match2){//匹配成功，直接放行
            return true;
        }

        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute != null){//查到了用户，说明已登录
            MemberRespVo memberRespVo = (MemberRespVo)attribute;
            loginUser.set(memberRespVo);//方便后面的方法获取登录对象，不用再去session中去取，进行判断一系列操作
            return  true;
        }else {
            session.setAttribute("msg", "请先进行登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
    }
}
