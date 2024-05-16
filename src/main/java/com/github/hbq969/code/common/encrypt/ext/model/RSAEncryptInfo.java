package com.github.hbq969.code.common.encrypt.ext.model;

import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * @author hbq969@gmail.com
 */
@Data
public class RSAEncryptInfo<T> {

    private String publicKey;
    private T obj;
    private String charset = "utf-8";

    public boolean valid() {
        return StringUtils.isNotBlank(publicKey) && !Objects.isNull(obj);
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
