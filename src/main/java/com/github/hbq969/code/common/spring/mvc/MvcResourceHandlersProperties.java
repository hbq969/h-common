package com.github.hbq969.code.common.spring.mvc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : Web mvc 资源映射扩展配置
 * @createTime : 2023/10/11 16:31
 */
@ConfigurationProperties(prefix = "spring.mvc.resource-handler-registry")
@Data
public class MvcResourceHandlersProperties {
    private List<ResourceHandler> entries;
}
