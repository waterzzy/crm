package com.waterz.crm.config;

import com.waterz.crm.interceptor.NoLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Bean // 给IOC管理
    public NoLoginInterceptor noLoginInterceptor() {
        return new NoLoginInterceptor();
    }

    public void addInterceptors( InterceptorRegistry registry) {
        // 需要⼀个实现HandlerInterceptor接⼝的拦截器实例，这⾥使⽤的是 NoLoginInterceptor
        registry.addInterceptor(noLoginInterceptor())
        // ⽤于设置拦截器的过滤路径规则
                .addPathPatterns("/**")// 默认拦截所有
        // ⽤于设置不需要拦截的过滤规则
                .excludePathPatterns("/index","/user/login","/css/**","/images/**","/js/**","/lib/**");
    }
}
