package com.github.hbq969.code.common.log.model;

import com.github.hbq969.code.common.log.api.Log;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

@AllArgsConstructor
@Data
public class PointModel {
    private Log log;
    private Method method;
    private String[] parameterNames;
    private Object[] parameterValues;
    private Object result;
    private Throwable rex;
    private Object target;
}
