package com.github.hbq969.code.common.encrypt.web;

import cn.hutool.core.map.MapUtil;
import com.github.hbq969.code.common.config.EncryptProperties;
import com.github.hbq969.code.common.encrypt.ext.model.AESInfo;
import com.github.hbq969.code.common.encrypt.ext.utils.AESUtil;
import com.github.hbq969.code.common.encrypt.ext.utils.Base64Util;
import com.github.hbq969.code.common.encrypt.ext.utils.RSAUtil;
import com.github.hbq969.code.common.restful.ICommonControl;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

/**
 * @author hbq969@gmail.com
 */
@Api(description = "接口aes对称加解密", tags = "维护使用-AES加解密")
@RequestMapping(path = "/hbq969-common/encrypt/restful/aes")
@Slf4j
public class AESControl implements ICommonControl {

    @Autowired
    private EncryptProperties conf;

    @ApiOperation("获取AES随机码")
    @RequestMapping(path = "/getRandomKey", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<String> getRandomKey(
            @RequestParam(name = "len", defaultValue = "16") Integer len) {
        try {
            return ReturnMessage.success(AESUtil.randomKeyWithAES(len));
        } catch (Exception e) {
            log.error("获取AES随机码异常", e);
            return ReturnMessage.fail("获取AES随机码异常");
        }
    }

    @ApiOperation("使用aes加密")
    @RequestMapping(path = "/encrypt", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> encrypt(@RequestBody AESInfo info) {
        try {
            return ReturnMessage.success(
                    AESUtil.encrypt(info.getContent(), info.getKey(), info.getIv(), Charset.forName(info.getCharset())));
        } catch (Exception e) {
            log.error("使用aes加密异常", e);
            return ReturnMessage.fail("使用aes加密异常");
        }
    }

    @ApiOperation("使用aes解密")
    @RequestMapping(path = "/decrypt", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> decrypt(@RequestBody AESInfo info) {
        try {
            return ReturnMessage.success(
                    AESUtil.decrypt(info.getContent(), info.getKey(), info.getIv(), Charset.forName(info.getCharset())));
        } catch (Exception e) {
            log.error("使用aes解密异常", e);
            return ReturnMessage.fail("使用aes解密异常");
        }
    }

    @ApiOperation("RAS样例解密")
    @RequestMapping(path = "/ras/decrypt", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<Map> ras(
            @RequestBody Map map) {
        String keyRSA = MapUtil.getStr(map, "keyRSA");
        String ivRSA = MapUtil.getStr(map, "ivRSA");
        String bodyEncode = MapUtil.getStr(map, "bodyEncode");
        String privateKey = conf.getRestful().getRsa().getPrivateKey();
        String charset = conf.getRestful().getAes().getCharset();
        try {
            String key = new String(RSAUtil.decrypt(Base64Util.decode(keyRSA), privateKey),
                    charset);
            String iv = new String(RSAUtil.decrypt(Base64Util.decode(ivRSA), privateKey),
                    charset);
            String body = AESUtil.decrypt(bodyEncode, key, iv, Charset.forName(conf.getRestful().getAes().getCharset()));
            return ReturnMessage.success(ImmutableMap.of("key", key, "iv", iv, "body", body));
        } catch (Exception e) {
            log.error("", e);
            return ReturnMessage.success(Collections.EMPTY_MAP);
        }
    }
}
