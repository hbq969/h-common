package com.github.hbq969.code.common.restful.version;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author hbq
 */
public class VersionRequestMappingHandler extends RequestMappingHandlerMapping {

    private final static Pattern VERSION_PREFIX_PATTERN = Pattern.compile("v[0-9]+\\.[0-9]+");

    @Nullable
    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        Version apiVersion = AnnotationUtils.findAnnotation(handlerType, Version.class);
        return createRequestCondition(apiVersion);
    }

    @Nullable
    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        Version apiVersion = AnnotationUtils.findAnnotation(method, Version.class);
        return createRequestCondition(apiVersion);
    }

    private RequestCondition<VersionCondition> createRequestCondition(Version version) {
        if (Objects.isNull(version)) {
            return null;
        }
        String value = version.value();
        Matcher matcher = VERSION_PREFIX_PATTERN.matcher(value);
        Assert.isTrue(matcher.matches(), "please check version, like v1.0");
        return new VersionCondition(value);
    }
}
