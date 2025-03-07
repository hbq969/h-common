package com.github.hbq969.code.common.spring.cloud.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import feign.Response;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

@Slf4j
public class TestDecoder implements Decoder {

    private final ObjectMapper mapper = new ObjectMapper();

    public TestDecoder() {
    }

    @Override
    public Object decode(Response response, Type type) throws IOException {
        if (response.body() == null) {
            return null;
        } else {
            Reader reader = response.body().asReader();
            if (!(reader).markSupported()) {
                reader = new BufferedReader(reader, 1);
            }

            try {
                (reader).mark(1);
                if ((reader).read() == -1) {
                    return null;
                } else {
                    (reader).reset();
                    log.info("+++++ : {}", IOUtils.toString(reader));
                    return null;
                }
            } catch (RuntimeJsonMappingException var5) {
                RuntimeJsonMappingException e = var5;
                if (e.getCause() != null && e.getCause() instanceof IOException) {
                    throw IOException.class.cast(e.getCause());
                } else {
                    throw e;
                }
            }
        }
    }
}