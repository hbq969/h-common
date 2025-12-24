package com.github.hbq969.code.common.cache.generator;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.github.hbq969.code.common.cache.Expire;
import com.github.hbq969.code.common.cache.ExpireKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存KEY生成器[API接口]
 * @createTime : 15:05:29, 2023.03.31, 周五
 */
@Slf4j
public class ApiKeyGenerator extends SimpleKeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        Expire expire = AnnotationUtils.findAnnotation(method, Expire.class);
        if (expire == null)
            return super.generate(target, method, params);
        String key = StrUtil.format("{}.{}", target.getClass().getName(), expire.methodKey());
        Object[] objects = new Object[1 + ArrayUtil.length(params)];
        objects[0] = key;
        Object[] news = ArrayUtil.append(objects, params);
        key = new SimpleKey(news).toString();
        if (AnnotationUtils.findAnnotation(method, CacheEvict.class) != null)
            log.debug("cleaning {}", key);
        else
            log.debug("caching {}, ({}/{})", key, expire.time(), expire.unit());
        return new ExpireKey(key, expire.time(), expire.unit(), System.currentTimeMillis());
    }

}
