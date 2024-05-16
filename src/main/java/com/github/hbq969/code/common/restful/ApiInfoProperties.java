package com.github.hbq969.code.common.restful;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : swagger接口元数据定义
 * @createTime : 17:58:06, 2023.03.29, 周三
 */
@ConfigurationProperties(prefix = "swagger.api-info", ignoreInvalidFields = true)
@Data
public class ApiInfoProperties {
    private boolean enable = true;
    private String title = "web应用Restful API";
    private String description = "web应用Restful API";
    private String version = "v1.0";
    private String license = "";
    private String licenseUrl = "";
}
