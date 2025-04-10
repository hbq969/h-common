package com.github.hbq969.code.common.encrypt.ext.advice;

import cn.hutool.core.map.MapUtil;
import com.github.hbq969.code.common.config.EncryptProperties;
import com.github.hbq969.code.common.encrypt.ext.config.Algorithm;
import com.github.hbq969.code.common.encrypt.ext.config.Decrypt;
import com.github.hbq969.code.common.encrypt.ext.exception.EncryptRequestException;
import com.github.hbq969.code.common.encrypt.ext.utils.AESUtil;
import com.github.hbq969.code.common.encrypt.ext.utils.Base64Util;
import com.github.hbq969.code.common.encrypt.ext.utils.JsonUtils;
import com.github.hbq969.code.common.encrypt.ext.utils.RSAUtil;
import com.github.hbq969.code.common.utils.GsonUtils;
import com.github.hbq969.code.common.utils.MDCUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hbq969@gmail.com
 **/
@Slf4j
public class DecryptHttpInputMessage implements HttpInputMessage {

    private HttpHeaders headers;

    private InputStream body;


    public DecryptHttpInputMessage(HttpInputMessage inputMessage, EncryptProperties conf, Decrypt decrypt) throws Exception {

        if (decrypt.algorithm() == Algorithm.AES) {
            decryptWithAES(inputMessage, conf);
        }

        if (decrypt.algorithm() == Algorithm.RSA) {
            decryptWithRSA(inputMessage, conf, decrypt);
        }

        if (decrypt.algorithm() == Algorithm.RAS) {
            decryptWithRAS(inputMessage, conf, decrypt);
        }

    }

    private void decryptWithRAS(HttpInputMessage inputMessage, EncryptProperties conf, Decrypt decrypt) throws Exception {
        String privateKey = conf.getRestful().getRsa().getPrivateKey();
        String charset = conf.getRestful().getRsa().getCharset();
        boolean showLog = conf.getRestful().getRsa().isShowLog();
        boolean timestampCheck = conf.getRestful().getRsa().isTimestampCheck();

        if (StringUtils.isEmpty(privateKey)) {
            throw new IllegalArgumentException("<请求>, 未配置rsa私钥");
        }
        this.headers = inputMessage.getHeaders();
        String content = new BufferedReader(new InputStreamReader(inputMessage.getBody())).lines().collect(Collectors.joining(System.lineSeparator()));
        Map map = GsonUtils.fromJson(content, Map.class);
        if (MapUtil.isEmpty(map))
            throw new UnsupportedOperationException("<请求>, 不支持的请求格式");

        String key = MapUtil.getStr(map, "key");
        String iv = MapUtil.getStr(map, "iv");
        String body = MapUtil.getStr(map, "body");
        if (showLog && log.isDebugEnabled())
            log.debug("<请求>, rsa解密前, key: {}, iv: {}, body: {}", key, iv, body);

        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(iv) || StringUtils.isEmpty(body))
            throw new UnsupportedOperationException("<请求>, 不支持的请求格式，必须包含key, iv, body");

        Charset c = Charset.forName(charset);
        byte[] keyBuf = RSAUtil.decrypt(Base64Util.decode(key), privateKey);
        byte[] ivBuf = RSAUtil.decrypt(Base64Util.decode(iv), privateKey);
        key = new String(keyBuf, c);
        iv = new String(ivBuf, c);
        if (showLog && log.isDebugEnabled())
            log.debug("<请求>, rsa解密后, key: {}, iv: {}", key, iv);

        MDCUtils.set("restful,aes,key", key);
        MDCUtils.set("restful,aes,iv", iv);
        if (showLog && log.isDebugEnabled())
            log.debug("<请求>, ras解密前: {}", body);

        String decryptBody = AESUtil.decrypt(body, key, iv, Charset.forName(charset));

        if (showLog && log.isDebugEnabled())
            log.debug("<请求>, ras解密后: {}", decryptBody);

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

    private void decryptWithRSA(HttpInputMessage inputMessage, EncryptProperties conf, Decrypt decrypt) throws Exception {
        String privateKey = conf.getRestful().getRsa().getPrivateKey();
        String charset = conf.getRestful().getRsa().getCharset();
        boolean showLog = conf.getRestful().getRsa().isShowLog();
        boolean timestampCheck = conf.getRestful().getRsa().isTimestampCheck();

        if (StringUtils.isEmpty(privateKey)) {
            throw new IllegalArgumentException("<请求>, 不支持的请求格式");
        }

        this.headers = inputMessage.getHeaders();
        String content = new BufferedReader(new InputStreamReader(inputMessage.getBody())).lines().collect(Collectors.joining(System.lineSeparator()));

        if (showLog && log.isDebugEnabled())
            log.debug("<请求>, rsa解密前: {}", content);

        String decryptBody;
        // 未加密内容
        if (content.startsWith("{")) {
            // 必须加密
            if (decrypt.required()) {
                log.error("不支持的rsa解密内容: {}", content);
                throw new EncryptRequestException("<请求>, 不支持rsa解密内容");
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
            if (showLog && log.isDebugEnabled())
                log.debug("<请求>, rsa解密后: {}", decryptBody);
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

    private void decryptWithAES(HttpInputMessage inputMessage, EncryptProperties conf) throws IOException {
        String key = conf.getRestful().getAes().getKey();
        String iv = conf.getRestful().getAes().getIv();
        String charset = conf.getRestful().getAes().getCharset();
        boolean showLog = conf.getRestful().getAes().isShowLog();

        if (StringUtils.isBlank(key) || StringUtils.isEmpty(iv)) {
            throw new IllegalArgumentException("<请求>,  aes私钥、iv未配置");
        }
        this.headers = inputMessage.getHeaders();
        String content = IOUtils.toString(inputMessage.getBody(), Charset.forName(charset));

        if (showLog && log.isDebugEnabled())
            log.debug("<请求>, aes解密前: {}", content);

        Charset c = Charset.forName(charset);
        String decryptBody = AESUtil.decrypt(content, key, iv, c);

        if (showLog && log.isDebugEnabled())
            log.debug("<请求>, aes解密后: {}", decryptBody);

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
