package com.github.hbq969.code.common.encrypt.ext.advice;

import com.github.hbq969.code.common.config.EncryptProperties;
import com.github.hbq969.code.common.encrypt.ext.config.Algorithm;
import com.github.hbq969.code.common.encrypt.ext.config.Encrypt;
import com.github.hbq969.code.common.encrypt.ext.utils.AESUtil;
import com.github.hbq969.code.common.encrypt.ext.utils.Base64Util;
import com.github.hbq969.code.common.encrypt.ext.utils.JsonUtils;
import com.github.hbq969.code.common.encrypt.ext.utils.RSAUtil;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * @author hbq969@gmail.com
 **/
//@ConditionalOnProperty(prefix = "encrypt.restful",name = "enabled",havingValue = "true")
@Slf4j
public class EncryptResponseBodyAdvice implements ControllerAdviceRemark, ResponseBodyAdvice<Object> {

    @Autowired
    private EncryptProperties conf;

    @Autowired
    private SpringContext context;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = returnType.getMethod();
        if (Objects.isNull(method)) {
            return false;
        }
        String controllerPackage = returnType.getContainingClass().getName();
        boolean flg = conf.getRestful().supportPackage(controllerPackage);
        return method.isAnnotationPresent(Encrypt.class) && flg;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Encrypt encrypt = returnType.getMethodAnnotation(Encrypt.class);
        if (encrypt.algorithm() == Algorithm.AES) {
            String key = conf.getRestful().getAes().getKey();
            String iv = conf.getRestful().getAes().getIv();
            if (conf.getRestful().getAes().getWay() == EncryptProperties.Restful.AES.WAY.THREAD_LOCAL) {
                key = MDCUtils.rmAndGet("restful,aes,key");
                iv = MDCUtils.rmAndGet("restful,aes,iv");
                if (StringUtils.isEmpty(key) || StringUtils.isEmpty(iv)) {
                    throw new UnsupportedOperationException("接收请求时未使用@Decrypt(algorithm=Algorithm.RAS)方式，线程上下文中无法获取key、iv，无法支持encrypt.restful.aes.way: THREAD_LOCAL策略");
                }
            }
            String charset = conf.getRestful().getAes().getCharset();
            try {
                String content = JsonUtils.writeValueAsString(body);
                if (StringUtils.isBlank(key)) {
                    throw new IllegalArgumentException("aes加密的密钥不能为空");
                }
                if (StringUtils.isBlank(charset)) {
                    charset = "utf-8";
                }
                String encryptBody = AESUtil.encrypt(content, key, iv, Charset.forName(charset));
                if (conf.getRestful().getAes().isShowLog()) {
                    if (log.isDebugEnabled()) log.debug("请求响应, aes加密前: {}, 加密后: {}", content, encryptBody);
                }
                return encryptBody;
            } catch (Exception e) {
                log.error("加密数据异常", e);
            }
        }

        if (encrypt.algorithm() == Algorithm.RSA) {
            String publicKey = conf.getRestful().getRsa().getPublicKey();
            String charset = conf.getRestful().getRsa().getCharset();
            try {
                if (StringUtils.isBlank(publicKey)) {
                    throw new IllegalArgumentException("rsa加密的公钥不能为空");
                }
                if (StringUtils.isBlank(charset)) {
                    charset = "utf-8";
                }
                String content = JsonUtils.writeValueAsString(body);
                byte[] data = content.getBytes(charset);
                byte[] encodedData = RSAUtil.encrypt(data, publicKey);
                String result = Base64Util.encode(encodedData);
                if (conf.getRestful().getRsa().isShowLog()) {
                    if (log.isDebugEnabled()) log.debug("请求响应, rsa加密前：{}，加密后：{}", content, result);
                }
                return result;
            } catch (Exception e) {
                log.error("加密数据异常", e);
            }
        }
        return body;
    }
}
