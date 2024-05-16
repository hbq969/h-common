package com.github.hbq969.code.common.encrypt.ext.advice;

import com.github.hbq969.code.common.encrypt.ext.config.AESConfig;
import com.github.hbq969.code.common.encrypt.ext.config.Algorithm;
import com.github.hbq969.code.common.encrypt.ext.config.Decrypt;
import com.github.hbq969.code.common.encrypt.ext.config.RSAConfig;
import com.github.hbq969.code.common.encrypt.ext.exception.EncryptRequestException;
import com.github.hbq969.code.common.encrypt.ext.utils.AESUtil;
import com.github.hbq969.code.common.encrypt.ext.utils.Base64Util;
import com.github.hbq969.code.common.encrypt.ext.utils.JsonUtils;
import com.github.hbq969.code.common.encrypt.ext.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.*;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

/**
 * @author hbq969@gmail.com
 **/
@Slf4j
public class DecryptHttpInputMessage implements HttpInputMessage {

    private HttpHeaders headers;

    private InputStream body;


    public DecryptHttpInputMessage(HttpInputMessage inputMessage, RSAConfig rsaConfig, AESConfig aesConfig, Decrypt decrypt) throws Exception {

        if (aesConfig.isEnabled() && decrypt.algorithm() == Algorithm.AES) {
            decryptWithAES(inputMessage, aesConfig);
        }

        if (rsaConfig.isEnabled() && decrypt.algorithm() == Algorithm.RSA) {
            decryptWithRSA(inputMessage, rsaConfig, decrypt);
        }

    }

    private void decryptWithRSA(HttpInputMessage inputMessage, RSAConfig rsaConfig, Decrypt decrypt) throws Exception {
        String privateKey = rsaConfig.getPrivateKey();
        String charset = rsaConfig.getCharset();
        boolean showLog = rsaConfig.isShowLog();
        boolean timestampCheck = rsaConfig.isTimestampCheck();

        if (StringUtils.isEmpty(privateKey)) {
            throw new IllegalArgumentException("rsa私钥为空");
        }
        if (StringUtils.isBlank(charset)) {
            charset = "utf-8";
        }

        this.headers = inputMessage.getHeaders();
        String content = new BufferedReader(new InputStreamReader(inputMessage.getBody()))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        String decryptBody;
        // 未加密内容
        if (content.startsWith("{")) {
            // 必须加密
            if (decrypt.required()) {
                log.error("不支持的解密内容: {}", content);
                throw new EncryptRequestException("不支持的解密内容");
            }
            decryptBody = content;
        } else {
            StringBuilder json = new StringBuilder();
            content = content.replaceAll(" ", "+");

            if (!StringUtils.isEmpty(content)) {
                String[] contents = content.split("\\|");
                for (String value : contents) {
                    value = new String(RSAUtil.decrypt(Base64Util.decode(value), privateKey), charset);
                    json.append(value);
                }
            }
            decryptBody = json.toString();
            if (showLog) {
                log.info("接收请求, rsa解密前：{}, 解密后：{}", content, decryptBody);
            }
        }

        // 开启时间戳检查
        if (timestampCheck) {
            // 容忍最小请求时间
            long toleranceTime = System.currentTimeMillis() - decrypt.timeout();
            long requestTime = JsonUtils.getNode(decryptBody, "timestamp").asLong();
            // 如果请求时间小于最小容忍请求时间, 判定为超时
            if (requestTime < toleranceTime) {
                log.error("加密请求超时, 实际时间: {}, 请求发起时间: {}, 配置的超时时间: {}, 解密后的值：{}", toleranceTime, requestTime, decrypt.timeout(), decryptBody);
                throw new EncryptRequestException("请求超时，可能被篡改");
            }
        }

        this.body = new ByteArrayInputStream(decryptBody.getBytes());
    }

    private void decryptWithAES(HttpInputMessage inputMessage, AESConfig aesConfig) throws IOException {
        String key = aesConfig.getKey();
        String charset = aesConfig.getCharset();
        boolean showLog = aesConfig.isShowLog();

        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("aes加密私钥为空");
        }
        this.headers = inputMessage.getHeaders();
        String content = IOUtils.toString(inputMessage.getBody(), Charset.forName(charset));
        if (StringUtils.isBlank(charset)) {
            charset = "utf-8";
        }
        String decryptBody = AESUtil.decrypt(content, key, Charset.forName(charset));
        if (showLog) {
            log.info("接收请求, aes解密前：{}, 解密后：{}", content, decryptBody);
        }
        this.body = new ByteArrayInputStream(decryptBody.getBytes());
    }

    @Override
    public InputStream getBody() {
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
