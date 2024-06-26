package com.waterz.crm;

import com.alibaba.fastjson.JSON;
import com.waterz.crm.base.ResultInfo;
import com.waterz.crm.exceptions.AuthException;
import com.waterz.crm.exceptions.NoLoginException;
import com.waterz.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 全局异常统⼀处理
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    /**
     * ⽅法返回值类型
     * 1.视图
     * 2.JSON
     * 如何判断⽅法的返回类型：
     * 如果⽅法级别配置了 @ResponseBody 注解，表示⽅法返回的是JSON；
     * 反之，返回的是视图⻚⾯
     *
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        // 非法请求拦截
        /**
         * 判断异常类型
         * 如果是未登录异常，则先执⾏相关的拦截操作
         */
        if (ex instanceof NoLoginException) {
            // 如果捕获的是未登录异常，则重定向到登录⻚⾯
            ModelAndView mv = new ModelAndView("redirect:/index");
            return mv;
        }


        // 设置默认异常处理
        ModelAndView mv = new ModelAndView();
        mv.setViewName("error");
        mv.addObject("code", 500);
        mv.addObject("msg", "系统异常，请稍后再试...");

        // 判断 HandlerMethod
        if (handler instanceof HandlerMethod) {
            // 类型转换
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取⽅法上的 ResponseBody 注解对象
            ResponseBody responseBody = handlerMethod.getMethod().getDeclaredAnnotation(ResponseBody.class);
            // 判断 ResponseBody 注解是否存在 (如果不存在，表示返回的是视图;如果存在，表示返回的是JSON)
            if (null == responseBody) {
                /**
                 * ⽅法返回视图
                 */
                // 判断异常类型
                if (ex instanceof ParamsException) {
                    ParamsException p = (ParamsException) ex;
                    // 设置异常信息
                    mv.addObject("code", p.getCode());
                    mv.addObject("msg", p.getMsg());
                }else if (ex instanceof AuthException) {    // 认证异常
                    AuthException p = (AuthException) ex;
                    // 设置异常信息
                    mv.addObject("code", p.getCode());
                    mv.addObject("msg", p.getMsg());
                }
                return mv;
            }else {
                /**
                 * ⽅法上返回JSON
                 */
                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常，请重试！");

                // 判断异常类型是否是自定义异常
                if (ex instanceof ParamsException) {
                    ParamsException p = (ParamsException) ex;
                    // 设置异常信息
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                }else if (ex instanceof AuthException) { // 认证异常
                    AuthException p = (AuthException) ex;
                    // 设置异常信息
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                }

                // 设置响应类型及编码格式（响应JSON格式的数据）
                response.setContentType("application/json;charset=utf-8");
                // 得到输出流

                PrintWriter out = null;
                try {
                    // 得到输出流
                    out = response.getWriter();
                    // 将对象转换成JSON格式，
                    String json = JSON.toJSONString(resultInfo);
                    // 通过输出流输出
                    out.write(json);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return null;
            }

        }
        return mv;
    }
}
