package com.github.hbq969.code.common.encrypt.web;

import com.github.hbq969.code.common.encrypt.ext.model.AESInfo;
import com.github.hbq969.code.common.encrypt.ext.utils.AESUtil;
import com.github.hbq969.code.common.restful.ICommonControl;
import com.github.hbq969.code.common.restful.ReturnMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;

/**
 * @author hbq969@gmail.com
 */
@Api(description = "接口aes对称加解密", tags = "维护使用-AES加解密")
@RequestMapping(path = "/common/encrypt/restful/aes")
@Slf4j
public class AESControl implements ICommonControl {

    @ApiOperation("获取AES随机码")
    @RequestMapping(path = "/getRandomKey", method = RequestMethod.GET)
    @ResponseBody
    public ReturnMessage<String> getRandomKey(
            @RequestParam(name = "len", defaultValue = "16") Integer len) {
        log.info("获取AES随机码");
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
        log.info("使用aes加密: {}", info);
        try {
            return ReturnMessage.success(
                    AESUtil.encrypt(info.getContent(), info.getKey(), Charset.forName(info.getCharset())));
        } catch (Exception e) {
            log.error("使用aes加密异常", e);
            return ReturnMessage.fail("使用aes加密异常");
        }
    }

    @ApiOperation("使用aes解密")
    @RequestMapping(path = "/decrypt", method = RequestMethod.POST)
    @ResponseBody
    public ReturnMessage<String> decrypt(@RequestBody AESInfo info) {
        log.info("使用aes解密: {}", info);
        try {
            return ReturnMessage.success(
                    AESUtil.decrypt(info.getContent(), info.getKey(), Charset.forName(info.getCharset())));
        } catch (Exception e) {
            log.error("使用aes解密异常", e);
            return ReturnMessage.fail("使用aes解密异常");
        }
    }
}
