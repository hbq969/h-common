package com.github.hbq969.code.common.java.ext.jsr;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {IPValidator.class}
)
public @interface IP {
    String message() default "{javax.validation.constraints.IPValidator.message}";

    boolean batch() default false;

    String splitString() default "\n";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
