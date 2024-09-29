package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.spring.mvc.ResourceHandler;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : spring拦截器相关配置项
 * @createTime : 2024/10/13 10:29
 */
@ConfigurationProperties(prefix = "spring.mvc.interceptors")
@Data
public class SpringInterceptorProperties {
    private Info info = new Info();
    private Header header = new Header();
    private MDC mdc = new MDC();
    private ApiSafe apiSafe = new ApiSafe();
    private ResourceHandlerRegistry resourceHandlerRegistry = new ResourceHandlerRegistry();


    @Data
    public static class Info {
        /**
         * 是否启用，默认false
         */
        private boolean enabled = false;
    }

    @Data
    static class Header {
        /**
         * 是否启用，默认false
         */
        private boolean enabled = false;
    }

    @Data
    public static class MDC {
        /**
         * 是否启用，默认false
         */
        private boolean enabled = true;
    }

    @Data
    public static class ApiSafe {
        /**
         * 是否启用，默认false
         */
        private boolean enabled = false;
        /**
         * api安全控制请求头
         */
        private String headerName = "user_id";

        /**
         * api安全控制请求头值（不支持正则，历史命名问题）
         */
        private String headerValueRegex;

        /**
         * 需要拦截api安全接口控制的uri,不包含server.context-path前缀
         */
        private List<String> includePathPatterns;

        /**
         * includePathPatterns中不需要拦截uri,不包含server.context-path前缀
         */
        private List<String> excludePathPatterns;
    }

    @Data
    public static class ResourceHandlerRegistry {
        /**
         * 是否启用，默认true
         */
        private boolean enabled = true;

        /**
         * 资源映射配置
         */
        private List<ResourceHandler> entries;
    }
}
