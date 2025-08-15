package com.github.hbq969.code.common.restful;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author : hbq969@gmail.com
 * @description : 健康检查
 * @createTime : 2023/8/15 14:39
 */
@RequestMapping(path = "/common")
@Slf4j
@Tag(description = "健康检查", name = "维护使用-健康检查接口")
public class HealthControl implements ICommonControl {

    @Operation(summary = "健康检查")
    @RequestMapping(path = "/health", method = RequestMethod.GET)
    public String health() {
        if (log.isDebugEnabled()) {
            log.debug("心跳。");
        }
        return "OK";
    }
}
