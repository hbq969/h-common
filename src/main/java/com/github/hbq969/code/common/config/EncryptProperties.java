package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.encrypt.ext.utils.AESUtil;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 加解密相关配置项
 * @createTime : 2024/10/13 11:20
 */
@ConfigurationProperties(prefix = "encrypt")
@Data
@Slf4j
public class EncryptProperties {

    private Config config = new Config();

    private Restful restful = new Restful();

    @Data
    public static class Config {
        /**
         * 是否启用springboot配置加解密，默认 true
         */
        private boolean enabled = true;
    }

    @Data
    public static class Restful {
        /**
         * 是否启用springboot接口加解密，默认 false
         */
        private boolean enabled = false;

        /**
         * 扫描的包路径
         */
        private List<String> advicePackages = Lists.newArrayList("com");

        private AES aes = new AES();

        private RSA rsa = new RSA();

        public boolean supportPackage(String pn) {
            if (advicePackages == null) {
                return false;
            }
            for (String apn : advicePackages) {
                if (StringUtils.startsWith(pn, apn)) {
                    return true;
                }
            }
            return false;
        }

        @Data
        public static class AES {
            /**
             * 加解密编码，默认 utf-8
             */
            private volatile String charset = "utf-8";

            /**
             * 是否打印加解密过程日志
             */
            private volatile boolean showLog = false;

            /**
             * 缺省秘钥
             */
            private volatile String key = AESUtil.KEY;
        }

        @Data
        public static class RSA {
            /**
             * 私钥配置，解密一方使用
             */
            private volatile String privateKey;

            /**
             * 公钥配置，分发给加密一方使用
             */
            private volatile String publicKey;

            /**
             * 加解密编码，默认 utf-8
             */
            private volatile String charset = "utf-8";

            /**
             * 是否开启加解密过程日志
             */
            private volatile boolean showLog = false;

            /**
             * 请求数据时间戳校验时间差 超过指定时间的数据认定为伪造
             */
            private volatile boolean timestampCheck = false;
        }
    }
}
