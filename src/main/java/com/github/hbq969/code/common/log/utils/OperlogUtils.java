package com.github.hbq969.code.common.log.utils;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.log.model.PointModel;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.restful.ReturnState;
import com.github.hbq969.code.common.utils.GsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public final class OperlogUtils {

    public static String getMethodDesc(PointModel point) {
        Operation ao = AnnotationUtils.findAnnotation(point.getMethod(), Operation.class);
        String md = null;
        if (null != ao) {
            md = ao.summary();
        }
        if (StringUtils.isEmpty(md)) {
            Schema amp = AnnotationUtils.findAnnotation(point.getMethod(), Schema.class);
            if (null != amp) {
                md = amp.description();
            }
        }
        return StringUtils.isEmpty(md) ? StringUtils.EMPTY : md;
    }

    public static ReturnMessage getResult(PointModel point) {
        ReturnMessage result = new ReturnMessage();
        try {
            if (!point.getMethod().getReturnType().equals(ReturnMessage.class)) {
                result.setState(ReturnState.OK);
                result.setBody("返回结果为非ReturnMessage对象，无法识别");
                return result;
            }
            // 接口返回null
            if (Objects.isNull(point.getResult())) {
                result.setState(ReturnState.OK);
                result.setBody("返回结果为空");
                return result;
            }

            // 接口抛异常
            if (Objects.nonNull(point.getRex())) {
                result.setState(ReturnState.ERROR);
                if (Validator.hasChinese(point.getRex().getMessage())) {
                    result.setBody(point.getRex().getMessage());
                } else {
                    result.setBody("接口调用发生异常");
                }
                return result;
            }

            // 接口调用正常
            ReturnMessage rm = (ReturnMessage) point.getResult();
            result.setState(rm.getState());
            if (point.getLog().collectResult()) {
                if (Objects.isNull(rm.getBody())) {
                    result.setBody("");
                } else if (rm.getBody() instanceof String) {
                    result.setBody(rm.getBody());
                } else {
                    try {
                        result.setBody(GsonUtils.toJson(rm.getBody()));
                    } catch (Throwable e) {
                        result.setBody(String.valueOf(rm.getBody()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("采集请求返回结果异常", e);
        }
        return result;
    }

    public static String getHeaders(PointModel point) {
        ServletRequestAttributes holder = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Assert.notNull(holder, "无法获取http请求上下文,请检查是否在非io线程中执行此代码");
        HttpServletRequest req = holder.getRequest();
        Map<String, String> headers = new HashMap<>(30);
        Enumeration<String> enums = req.getHeaderNames();
        String headerKey;
        while (enums.hasMoreElements()) {
            headerKey = enums.nextElement();
            headers.put(headerKey, req.getHeader(headerKey));
        }
        try {
            return GsonUtils.toJson(headers);
        } catch (Throwable e) {
            return String.valueOf(headers);
        }
    }

    public static String getPostBody(PointModel point) {
        if (!point.getLog().collectPostBody()) {
            return StringUtils.EMPTY;
        }
        Parameter[] ps = point.getMethod().getParameters();
        if (ArrayUtil.isEmpty(ps))
            return StringUtils.EMPTY;
        for (int i = 0; i < ps.length; i++) {
            if (!ps[i].isAnnotationPresent(RequestBody.class))
                continue;
            try {
                return GsonUtils.toJson(point.getParameterValues()[i]);
            } catch (Exception e) {
                return point.getParameterValues()[i].toString();
            }
        }
        return StringUtils.EMPTY;
    }
}
