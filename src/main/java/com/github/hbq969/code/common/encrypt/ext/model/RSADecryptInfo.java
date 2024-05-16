package com.github.hbq969.code.common.encrypt.ext.model;

import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hbq969@gmail.com
 */
@Data
public class RSADecryptInfo {

    private String privateKey;
    private String encode;
    private String charset = "utf-8";

    public boolean valid() {
        return StringUtils.isNotBlank(privateKey) && StringUtils.isNotBlank(encode);
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
