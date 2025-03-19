package com.github.hbq969.code.common.encrypt.ext.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * @author hbq969@gmail.com
 */
@Slf4j
public class AESUtil {

    //长度必须为16、24、32位，即128bit、192bit、256bit
    public final static String KEY = "DC2EE8931E434A44";

    // 长度必须为16位
    public final static String IV = "9F0805A762E542EB";

    private final static Charset CHARSET = Charset.forName("utf-8");

    private final static int OFFSET = 16;

    private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final static String ALGORITHM = "AES";

    public static String encrypt(String content) {
        return encrypt(content, KEY, CHARSET);
    }

    public static String encrypt(String content, Charset charset) {
        return encrypt(content, KEY, charset);
    }

    public static String encrypt(String content, String key) {
        return encrypt(content, key, CHARSET);
    }

    public static String decrypt(String content) {
        return decrypt(content, KEY, CHARSET);
    }

    public static String decrypt(String content, String key) {
        return decrypt(content, key, CHARSET);
    }

    public static String decrypt(String content, Charset charset) {
        return decrypt(content, KEY, charset);
    }

    public static String encrypt(String content, String key, Charset charset) {
        return encrypt(content, key, key, charset);
    }

    public static String encrypt(String content, String key, String iv, Charset charset) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(key)
                || StringUtils.isEmpty(iv) || Objects.isNull(charset)) {
            throw new IllegalArgumentException("content, key, iv, charset 为空,请检查");
        }
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            IvParameterSpec ivp = new IvParameterSpec(iv.getBytes(), 0, OFFSET);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            byte[] byteContent = content.getBytes(charset.name());
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivp);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return Base64Util.encode(result);
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public static String decrypt(String content, String key, Charset charset) {
        return decrypt(content, key, key, charset);
    }

    public static String decrypt(String content, String key, String iv, Charset charset) {
        if (StringUtils.isBlank(content) || StringUtils.isBlank(key)
                || StringUtils.isEmpty(iv) || Objects.isNull(charset)) {
            throw new IllegalArgumentException("content, key, iv, charset 为空,请检查");
        }
        try {
            SecretKeySpec skey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            IvParameterSpec ivp = new IvParameterSpec(iv.getBytes(), 0, OFFSET);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, skey, ivp);// 初始化
            byte[] result = cipher.doFinal(Base64Util.decode(content));
            return new String(result, charset); // 解密
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    public final static String randomKeyWithAES(Integer len) {
        String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
        if (len == null) {
            return uuid.substring(0, 16);
        }
        if (len > 32) {
            return uuid;
        } else {
            return uuid.substring(0, len);
        }
    }


    public static void main(String[] args) {
        String str="foo";
        String encode = encrypt(str, "D2FC0454D40B4461", "C4417DC80C1445BE", StandardCharsets.UTF_8);
        System.out.println(encode);
        String decode = decrypt("UtJd5PwzcQi0isTj9V0Ey6BHfQCppfF6bikV2W3oYZMFTLxFcWP50z9Y752ZX3+U", "D2FC0454D40B4461", "C4417DC80C1445BE", StandardCharsets.UTF_8);
        System.out.println(decode);
    }
}
