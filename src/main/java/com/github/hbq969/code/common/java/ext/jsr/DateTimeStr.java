package com.github.hbq969.code.common.java.ext.jsr;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author : hbq969@gmail.com
 * @description : 日期格式校验
 * @createTime : 2023/8/30 11:39
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DateTimeValidator.class)
public @interface DateTimeStr {

  String message() default "{javax.validation.constraints.DateTimeStr.message}";

  String format() default "yyyy-MM-dd HH:mm:ss";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
