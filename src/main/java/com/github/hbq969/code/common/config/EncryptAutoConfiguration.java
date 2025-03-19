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
        return new EncryptProperties();
    }

    @ConditionalOnProperty(prefix = "encrypt.config", name = "enabled", havingValue = "true")
    @Bean("common-encrypt-config-SpringConfigEncryptCtrl")
    ConfigControl configControl() {
        return new ConfigControl();
    }


    @ConditionalOnProperty(prefix = "encrypt.config", name = "enabled", havingValue = "true")
    @Bean("jasyptStringEncryptor")
    @Primary
    StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        String pwd = environment.getProperty("jasypt.encryptor.password", "U(^3ia)*v2$");
        String algorithm = environment.getProperty("jasypt.encryptor.algorithm", "PBEWITHHMACSHA512ANDAES_256");
        String koi = environment.getProperty("jasypt.encryptor.key-obtention-iterations", "1000");
        String poolSize = environment.getProperty("jasypt.encryptor.pool-size", "1");
        String providerName = environment.getProperty("jasypt.encryptor.provider-name", "SunJCE");
        String sgcn = environment.getProperty("jasypt.encryptor.salt-generator-class-name", "org.jasypt.salt.RandomSaltGenerator");
        String sot = environment.getProperty("jasypt.encryptor.string-output-type", "base64");
        String igcn = environment.getProperty("jasypt.encryptor.iv-generator-class-name", "org.jasypt.iv.RandomIvGenerator");
        config.setPassword(pwd);
        config.setAlgorithm(algorithm);
        config.setKeyObtentionIterations(koi);
        config.setPoolSize(poolSize);
        config.setProviderName(providerName);
        config.setSaltGeneratorClassName(sgcn);
        config.setStringOutputType(sot);
        try {
            config.setIvGeneratorClassName(igcn);
        } catch (Error e) {
            log.error("SimpleStringPBEConfig方法setIvGeneratorClassName不兼容，向下兼容处理");
        }
        encryptor.setConfig(config);
        if (log.isDebugEnabled()) {
            log.debug("创建自定义的jasyptStringEncryptor。");
        }
        return encryptor;
    }

    @ConditionalOnProperty(prefix = "encrypt.restful", name = "enabled", havingValue = "true")
    @Bean("common-restful-AESCtrl")
    AESControl aesControl() {
        return new AESControl();
    }

    @ConditionalOnProperty(prefix = "encrypt.restful", name = "enabled", havingValue = "true")
    @Bean("common-restful-RSACtrl")
    RSAControl rsaControl() {
        return new RSAControl();
    }

    @ConditionalOnProperty(prefix = "encrypt.restful", name = "enabled", havingValue = "true")
    @Bean("common-spring-encrypt-encryptRequestBodyAdvice")
    public EncryptRequestBodyAdvice encryptRequestBodyAdvice() {
        if (log.isDebugEnabled()) {
            log.debug("启用restful接口加解密功能。");
        }
        return new EncryptRequestBodyAdvice();
    }

    @ConditionalOnProperty(prefix = "encrypt.restful", name = "enabled", havingValue = "true")
    @Bean("common-spring-encrypt-encryptResponseBodyAdvice")
    public EncryptResponseBodyAdvice encryptResponseBodyAdvice() {
        return new EncryptResponseBodyAdvice();
    }
}
