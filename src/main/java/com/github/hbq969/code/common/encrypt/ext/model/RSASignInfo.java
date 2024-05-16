package com.github.hbq969.code.common.encrypt.ext.model;

import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author hbq969@gmail.com
 */
@Data
public class RSASignInfo {

    private String base64PrivateKey;
    private String encodeData;

    public boolean hasData() {
        return StringUtils.isNotBlank(encodeData);
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
