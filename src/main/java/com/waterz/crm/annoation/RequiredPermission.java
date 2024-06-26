package com.waterz.crm.annoation;

import java.lang.annotation.*;

/**
 * 自定义注解
 *  定义方法需要的对应的权限码
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredPermission {
    // 权限码
    String code() default "";
}
