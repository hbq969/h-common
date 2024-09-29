package com.github.hbq969.code.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : swagger配置项
 * @createTime : 2024/10/13 13:45
 */
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerProperties {

    /**
     * swagger扫描包路径
     */
    private String basePackage = "com";

    private ApiInfo apiInfo = new ApiInfo();

    @Data
    public static class ApiInfo {

        /**
         * swagger api接口标题
         */
        private String title = "web应用Restful API";

        /**
         * swagger api接口描述
         */
        private String description = "web应用Restful API";

        /**
         * swagger api接口版本
         */
        private String version = "v1.0";

        /**
         * swagger api接口 LICENSE
         */
        private String license = "";

        /**
         * swagger api接口 LICENSE 地址
         */
        private String licenseUrl = "";

        /**
         * 是否添加server.servlet.context-path前缀
         */
        private boolean pathMapping = false;
    }
}
