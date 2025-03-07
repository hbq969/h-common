package com.github.hbq969.code.common.encrypt.ext.model;

import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.Data;

/**
 * @author hbq969@gmail.com
 */
@Data
public class AESInfo {

    private String key;
    private String iv;
    private String charset = "utf-8";
    private String content;

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
