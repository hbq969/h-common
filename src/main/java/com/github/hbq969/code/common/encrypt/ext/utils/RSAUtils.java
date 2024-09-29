package com.github.hbq969.code.common.encrypt.ext.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

/**
 * @author : hbq969@gmail.com
 * @description :
 * @createTime : 2024/9/21 15:14
 */
public class RSAUtils {
    // 密钥长度
    private static final int KEY_SIZE = 2048;
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 128;

    // 生成RSA密钥对
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(KEY_SIZE);
        return keyPairGen.generateKeyPair();
    }

    // 获取AES对称密钥
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    // AES加密
    public static String encryptAES(String data, SecretKey aesKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // AES解密
    public static String decryptAES(String encryptedData, SecretKey aesKey, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    // RSA加密AES密钥
    public static String encryptAESKey(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    // RSA解密AES密钥
    public static SecretKey decryptAESKey(String encryptedAESKey, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));
        return new SecretKeySpec(decodedKey, "AES");
    }

    public static void main(String[] args) throws Exception {
        // 生成 RSA 密钥对
        KeyPair rsaKeyPair = generateKeyPair();

        // 生成 AES 密钥和初始化向量
        SecretKey aesKey = generateAESKey();
        byte[] iv = new byte[16];  // 假设使用 16 字节 IV
        new SecureRandom().nextBytes(iv);  // 生成随机 IV

        String data = "原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111原数据123456，测试一下1111111111111111111111111111111111111222222222222222222221111111111111111111111111111111111111111111111111";

        // 用 AES 加密数据
        String aesEncryptedData = encryptAES(data, aesKey, iv);
        System.out.println("AES 加密后的数据: " + aesEncryptedData);

        // 用 RSA 加密 AES 密钥
        String encryptedAESKey = encryptAESKey(aesKey, rsaKeyPair.getPublic());
        System.out.println("RSA 加密的 AES 密钥: " + encryptedAESKey);

        // 用 RSA 解密 AES 密钥
        SecretKey decryptedAESKey = decryptAESKey(encryptedAESKey, rsaKeyPair.getPrivate());

        // 用 AES 解密数据
        String decryptedData = decryptAES(aesEncryptedData, decryptedAESKey, iv);
        System.out.println("AES 解密后的数据: " + decryptedData);
    }
}
