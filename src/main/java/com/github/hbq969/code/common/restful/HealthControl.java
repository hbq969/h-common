package com.github.hbq969.code.common.restful;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(description = "健康检查", tags = "健康检查接口")
public class HealthControl implements ICommonControl {

    @ApiOperation("健康检查")
    @RequestMapping(path = "/health", method = RequestMethod.GET)
    public String health() {
        log.debug("心跳。");
        return "OK";
    }
}
