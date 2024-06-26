package com.waterz.crm.interceptor;

import com.waterz.crm.exceptions.NoLoginException;
import com.waterz.crm.service.UserService;
import com.waterz.crm.utils.LoginUserUtil;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class NoLoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private UserService userService;
    /**
     * 拦截用户是否是登录状态
     *  在目标方法（目标资源）执行前，执行的方法
     *
     *  方法返回的是布尔类型
     *      如果返回true，表示目标方法可以被执行
     *      如果返回false，表示阻止目标方法执行
     *
     * 判断⽤户是否是登录状态
     * 获取Cookie对象，解析⽤户ID的值
     * 如果⽤户ID不为空，且在数据库中存在对应的⽤户记录，表示请求合法
     * 否则，请求不合法，进⾏拦截，重定向到登录⻚⾯
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 获取cookie中的用户ID
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        if (null == userId || null == userService.selectByPrimaryKey(userId)) {
            // 抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
