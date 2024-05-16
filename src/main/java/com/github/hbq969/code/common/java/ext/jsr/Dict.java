package com.github.hbq969.code.common.java.ext.jsr;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author : hbq969@gmail.com
 * @description : 字典校验
 * @createTime : 2023/8/30 13:33
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DictValidator.class)
public @interface Dict {

  String message() default "{javax.validation.constraints.DictValidator.message}";

  /**
   * 枚举值以逗号连接
   *
   * @return
   */
  String expectValue();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
