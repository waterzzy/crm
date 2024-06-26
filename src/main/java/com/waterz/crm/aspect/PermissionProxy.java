package com.waterz.crm.aspect;

import com.waterz.crm.annoation.RequiredPermission;
import com.waterz.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {

    @Resource
    private HttpSession session;
    /**
     * 切面会拦截指定包下的指定注解
     *  拦截com.waterz.crm.annoation.RequiredPermission的注解
     *
     *
     * @param pjp
     * @return
     */

    @Around(value = "@annotation(com.waterz.crm.annoation.RequiredPermission)")
    public Object arround(ProceedingJoinPoint pjp) throws Throwable {
        Object result = null;
        // 得到当前用户拥有的权限（session作用域中）
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        // 判断用户是否拥有这个权限
        if(null == permissions || permissions.size() < 1){
            // 抛出认证异常
            throw new AuthException();
        }

        // 得到对应的目标
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        //
        RequiredPermission requiredPermission = methodSignature.getMethod().getDeclaredAnnotation(RequiredPermission.class);
        //
        if(!(permissions.contains(requiredPermission.code()))){
            //
            throw new AuthException();
        }

        result = pjp.proceed();
        return result;
    }
}
