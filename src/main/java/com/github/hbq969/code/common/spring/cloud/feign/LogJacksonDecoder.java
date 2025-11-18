package com.github.hbq969.code.common.spring.cloud.feign;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;

@Slf4j
public class LogJacksonDecoder implements Decoder {
    private final ObjectMapper mapper;
    private final boolean canLog;

    public LogJacksonDecoder(boolean canLog) {
        this(Collections.<Module>emptyList(), canLog);
    }

    public LogJacksonDecoder(Iterable<Module> modules, boolean canLog) {
        this(new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModules(modules), canLog);
    }

    public LogJacksonDecoder(ObjectMapper mapper, boolean canLog) {
        this.mapper = mapper;
        this.canLog = canLog;
    }

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        if (response.body() == null)
            return null;
        Reader reader = response.body().asReader();
        if (!reader.markSupported()) {
            reader = new BufferedReader(reader, 1);
        }
        try {
            // Read the first byte to see if we have any data
            reader.mark(1);
            if (reader.read() == -1) {
                return null; // Eagerly returning null avoids "No content to map due to end-of-input"
            }
            reader.reset();
            if (canLog) {
                String content = IoUtil.read(reader);
                log.debug("[{}] 请求响应原始报文 +++ {}", response.request().url(), content);
                // 读取内容后需要重新创建 reader 用于反序列化
                return mapper.readValue(content, mapper.constructType(type));
            } else {
                return mapper.readValue(reader, mapper.constructType(type));
            }
        } catch (RuntimeJsonMappingException e) {
            if (e.getCause() != null && e.getCause() instanceof IOException) {
                throw IOException.class.cast(e.getCause());
            }
            throw e;
        }
    }
}
