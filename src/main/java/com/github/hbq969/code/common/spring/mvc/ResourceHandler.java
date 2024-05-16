package com.github.hbq969.code.common.spring.mvc;

import lombok.Data;

/**
 * @author : hbq969@gmail.com
 * @description : web mvc 映射
 * @createTime : 2023/10/11 16:42
 */
@Data
public class ResourceHandler {
    private String[] handlers;
    private String[] locations;
}
