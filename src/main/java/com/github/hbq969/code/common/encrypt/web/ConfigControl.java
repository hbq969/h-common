package com.github.hbq969.code.common.encrypt.web;

import com.github.hbq969.code.common.encrypt.model.EncryptInfo;
import com.github.hbq969.code.common.restful.ICommonControl;
import com.github.hbq969.code.common.restful.ReturnMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : hbq969@gmail.com
 * @description : Spring配置加解密辅助类
 * @createTime : 2023/6/25 21:31
 */
@RequestMapping(path = "/hbq969-common/encrypt/config")
@Slf4j
@Api(description = "spring配置属性加解密", tags = "维护使用-spring配置属性加解密接口")
public class ConfigControl implements ICommonControl {

    @ApiOperation("Spring配置加密")
    @RequestMapping(path = "/encrypt", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> encrypt(@RequestBody EncryptInfo info) {
        log.info("配置加密: {}", info);
        try {
            StringEncryptor enc = createEncrypt(info);
            return ReturnMessage.success(enc.encrypt(info.getData()));
        } catch (Exception e) {
            log.error("配置加密异常", e);
            return ReturnMessage.fail("配置加密异常");
        }
    }

    @ApiOperation("Spring配置解密")
    @RequestMapping(path = "/decrypt", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> decrypt(@RequestBody EncryptInfo info) {
        log.info("Spring配置解密: {}", info);
        try {
            StringEncryptor enc = createEncrypt(info);
            return ReturnMessage.success(enc.decrypt(info.getData()));
        } catch (Exception e) {
            log.error("Spring配置解密异常", e);
            return ReturnMessage.fail("Spring配置解密异常");
        }
    }

    private static StringEncryptor createEncrypt(EncryptInfo info) {
        PooledPBEStringEncryptor enc = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        String pwd = info.getKey();
        config.setPassword(StringUtils.isEmpty(pwd) ? "U(^3ia)*v2$" : pwd);
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        enc.setConfig(config);
        return enc;
    }
}
