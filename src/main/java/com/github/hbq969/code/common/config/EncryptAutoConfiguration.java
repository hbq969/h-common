package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.encrypt.ext.advice.EncryptRequestBodyAdvice;
import com.github.hbq969.code.common.encrypt.ext.advice.EncryptResponseBodyAdvice;
import com.github.hbq969.code.common.encrypt.ext.config.AESConfig;
import com.github.hbq969.code.common.encrypt.ext.config.RSAConfig;
import com.github.hbq969.code.common.encrypt.web.AESControl;
import com.github.hbq969.code.common.encrypt.web.ConfigControl;
import com.github.hbq969.code.common.encrypt.web.RSAControl;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

/**
 * @author : hbq969@gmail.com
 * @description : restful接口加解密自动装配
 * @createTime : 2023/8/27 10:35
 */
@Slf4j
public class EncryptAutoConfiguration implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @ConditionalOnExpression("${encrypt.config.enabled:false}")
    @Bean("common-encrypt-config-SpringConfigEncryptCtrl")
    ConfigControl configControl() {
        return new ConfigControl();
    }

    @Bean("jasyptStringEncryptor")
    @ConditionalOnExpression("${encrypt.config.enabled:true}")
    @Primary
    StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        String pwd = environment.getProperty("jasypt.encryptor.password", "U(^3ia)*v2$");
        config.setPassword(pwd);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    @ConditionalOnExpression("${encrypt.restful.aes.enabled:false}")
    @Bean("common-restful-AESCtrl")
    AESControl aesControl() {
        return new AESControl();
    }

    @ConditionalOnExpression("${encrypt.restful.rsa.enabled:false}")
    @Bean("common-restful-RSACtrl")
    RSAControl rsaControl() {
        return new RSAControl();
    }

    @Bean("common-spring-encrypt-aesConfig")
    public AESConfig aesConfig() {
        return new AESConfig();
    }

    @Bean("common-spring-encrypt-rsaConfig")
    public RSAConfig rsaConfig() {
        return new RSAConfig();
    }

    @Bean("common-spring-encrypt-encryptRequestBodyAdvice")
    @ConditionalOnExpression("${encrypt.restful.aes.enabled:false}")
    public EncryptRequestBodyAdvice encryptRequestBodyAdvice() {
        log.info("启用restful接口对称加密");
        return new EncryptRequestBodyAdvice();
    }

    @Bean("common-spring-encrypt-encryptResponseBodyAdvice")
    @ConditionalOnExpression("${encrypt.restful.rsa.enabled:false}")
    public EncryptResponseBodyAdvice encryptResponseBodyAdvice() {
        log.info("启用restful接口非对称加密");
        return new EncryptResponseBodyAdvice();
    }
}
