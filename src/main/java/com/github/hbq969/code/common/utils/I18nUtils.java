package com.github.hbq969.code.common.utils;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.spring.context.SpringContext;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class I18nUtils {
    public static String getMessage(SpringContext context, String code, Object[] args) {
        MessageSource messageSource = context.getBean(MessageSource.class);
        Assert.notNull(messageSource, "Not inject MessageSource");
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    public static String getMessage(SpringContext context, String code) {
        return getMessage(context, code, null);
    }
}
