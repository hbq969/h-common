package com.github.hbq969.code.common.spring.advice.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : aop配置项
 * @createTime : 2024/10/13 10:33
 */
@ConfigurationProperties(prefix = "advice")
@Data
public class AdviceProperties {

    private Log log;

    private Ex ex;

    private RestfulLimit restfulLimit;

    @Data
    public static class Log {
        /**
         * 是否启用，默认true
         */
        private boolean enabled = true;
    }

    @Data
    public static class Ex {
        /**
         * 是否启用，默认true
         */
        private boolean enabled = true;
    }

    @Data
    public static class RestfulLimit {
        /**
         * 是否启用，默认false
         */
        private boolean enabled = false;
    }
}
