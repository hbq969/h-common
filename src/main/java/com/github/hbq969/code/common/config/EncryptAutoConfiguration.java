package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.encrypt.ext.advice.EncryptRequestBodyAdvice;
import com.github.hbq969.code.common.encrypt.ext.advice.EncryptResponseBodyAdvice;
import com.github.hbq969.code.common.encrypt.web.AESControl;
import com.github.hbq969.code.common.encrypt.web.ConfigControl;
import com.github.hbq969.code.common.encrypt.web.RSAControl;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    @Bean("common-EncryptProperties")
    EncryptProperties encryptProperties() {
        log.info("初始化 EncryptProperties");
        return new EncryptProperties();
    }

    @ConditionalOnProperty(prefix = "encrypt.config",name = "enabled",havingValue = "true")
    @Bean("common-encrypt-config-SpringConfigEncryptCtrl")
    ConfigControl configControl() {
        return new ConfigControl();
    }


    @ConditionalOnProperty(prefix = "encrypt.config",name = "enabled",havingValue = "true")
    @Bean("jasyptStringEncryptor")
    @Primary
    StringEncryptor stringEncryptor() {
        log.info("初始化自定义的 jasyptStringEncryptor.");
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

    @ConditionalOnProperty(prefix = "encrypt.restful",name = "enabled",havingValue = "true")
    @Bean("common-restful-AESCtrl")
    AESControl aesControl() {
        return new AESControl();
    }

    @ConditionalOnProperty(prefix = "encrypt.restful",name = "enabled",havingValue = "true")
    @Bean("common-restful-RSACtrl")
    RSAControl rsaControl() {
        return new RSAControl();
    }

    @ConditionalOnProperty(prefix = "encrypt.restful",name = "enabled",havingValue = "true")
    @Bean("common-spring-encrypt-encryptRequestBodyAdvice")
    public EncryptRequestBodyAdvice encryptRequestBodyAdvice() {
        log.info("启用restful接口对称加密");
        return new EncryptRequestBodyAdvice();
    }

    @ConditionalOnProperty(prefix = "encrypt.restful",name = "enabled",havingValue = "true")
    @Bean("common-spring-encrypt-encryptResponseBodyAdvice")
    public EncryptResponseBodyAdvice encryptResponseBodyAdvice() {
        log.info("启用restful接口非对称加密");
        return new EncryptResponseBodyAdvice();
    }
}
