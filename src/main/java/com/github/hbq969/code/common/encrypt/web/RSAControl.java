package com.github.hbq969.code.common.encrypt.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hbq969.code.common.encrypt.ext.model.RSADecryptInfo;
import com.github.hbq969.code.common.encrypt.ext.model.RSAEncryptInfo;
import com.github.hbq969.code.common.encrypt.ext.model.RSASignInfo;
import com.github.hbq969.code.common.encrypt.ext.utils.Base64Util;
import com.github.hbq969.code.common.encrypt.ext.utils.RSAUtil;
import com.github.hbq969.code.common.restful.ICommonControl;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

/**
 * @author hbq969@gmail.com
 */
@RequestMapping(path = "/hbq969-common/encrypt/restful/rsa")
@Slf4j
@Api(description = "接口rsa非对称加解密", tags = "维护使用-RSA加解密")
public class RSAControl implements ICommonControl {

    @ApiOperation("获取密钥对")
    @RequestMapping(path = "/genKeyPair", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<Map> genKeyPair() {
        log.info("获取密钥对");
        try {
            Map keyPair = RSAUtil.genKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.get(RSAUtil.PUBLIC_KEY);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.get(RSAUtil.PRIVATE_KEY);
            return ReturnMessage.success(
                    ImmutableMap.of(RSAUtil.PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()),
                            RSAUtil.PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded())));
        } catch (Exception e) {
            log.error("获取密钥对异常", e);
            return ReturnMessage.fail("获取密钥对异常");
        }
    }

    @ApiOperation("对加密的内容进行签名")
    @RequestMapping(path = "/sign", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> sign(@RequestBody RSASignInfo info) {
        log.info("对加密的内容进行签名: {}", info);
        try {
            if (info.hasData()) {
                return ReturnMessage.success(
                        RSAUtil.sign(info.getEncodeData().getBytes(), info.getBase64PrivateKey()));
            } else {
                return ReturnMessage.fail("签名失败");
            }
        } catch (Exception e) {
            log.error("对加密的内容进行签名异常", e);
            return ReturnMessage.fail("对加密的内容进行签名异常");
        }
    }

    @ApiOperation("使用公钥加密")
    @RequestMapping(path = "/encryptWithPublicKey", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> encryptWithPublicKey(@RequestBody RSAEncryptInfo info) {
        log.info("使用公钥加密: {}", info);
        try {
            if (info.valid()) {
                String content = String.valueOf(info.getObj());
                byte[] data = content.getBytes(info.getCharset());
                data = RSAUtil.encrypt(data, info.getPublicKey());
                String result = Base64Util.encode(data);
                return ReturnMessage.success(result);
            } else {
                return ReturnMessage.fail("加密失败");
            }
        } catch (Exception e) {
            log.error("使用公钥加密异常", e);
            return ReturnMessage.fail("使用公钥加密异常");
        }
    }

    @ApiOperation("使用私钥解密")
    @RequestMapping(path = "/decryptWithPrivateKey", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> decryptWithPrivateKey(@RequestBody RSADecryptInfo info) {
        log.info("使用私钥解密: {}", info);
        try {
            String content = info.getEncode();
            if (info.valid()) {
                StringBuilder result = new StringBuilder();
                content = content.replaceAll(" ", "+");
                if (!StringUtils.isEmpty(content)) {
                    String[] contents = content.split("\\|");
                    for (String value : contents) {
                        value = new String(RSAUtil.decrypt(Base64Util.decode(value), info.getPrivateKey()),
                                info.getCharset());
                        result.append(value);
                    }
                }
                log.info("++++++ {}",result);
                return ReturnMessage.success(result.toString());
            } else {
                return ReturnMessage.fail("解码失败");
            }
        } catch (Exception e) {
            log.error("使用私钥解密异常", e);
            return ReturnMessage.fail("使用私钥解密异常");
        }
    }
}
