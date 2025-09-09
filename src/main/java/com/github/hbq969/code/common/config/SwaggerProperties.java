package com.github.hbq969.code.common.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * api分组名称
     */
    private String group = "default";

    private Info apiInfo = new Info();

    private List<Server> servers = new ArrayList<>();

    /**
     * 是否添加server.servlet.context-path前缀
     */
    private boolean pathMapping = false;
}
