package com.github.hbq969.code.common.encrypt.ext.advice;

import com.github.hbq969.code.common.config.EncryptProperties;
import com.github.hbq969.code.common.encrypt.ext.config.Decrypt;
import com.github.hbq969.code.common.encrypt.ext.exception.EncryptRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author hbq969@gmail.com
 **/
//@ConditionalOnProperty(prefix = "encrypt.restful",name = "enabled",havingValue = "true")
@Slf4j
public class EncryptRequestBodyAdvice implements ControllerAdviceRemark, RequestBodyAdvice {

    @Autowired
    private EncryptProperties conf;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = methodParameter.getMethod();
        String controllerPackage = methodParameter.getContainingClass().getName();
        return method.isAnnotationPresent(Decrypt.class)
                && conf.getRestful().supportPackage(controllerPackage);
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) {
        try {
            // 检查请求体是否已经被处理
            if (inputMessage instanceof DecryptHttpInputMessage) {
                return inputMessage;
            }
            return new DecryptHttpInputMessage(inputMessage, conf, parameter.getMethod().getAnnotation(Decrypt.class));
        } catch (EncryptRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Decryption failed", e);
        }
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
