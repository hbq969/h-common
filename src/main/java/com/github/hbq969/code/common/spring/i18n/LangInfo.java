package com.github.hbq969.code.common.spring.i18n;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@AllArgsConstructor
@Data
public class LangInfo {
    private String app;
    private String lang;

    public String filename(String prefix, String suffix) {
        if (!StringUtils.endsWith(prefix, "-") && StringUtils.isNotEmpty(lang)) {
            prefix += "-";
        }
        if (!StringUtils.startsWith(suffix, ".")) {
            suffix = "." + suffix;
        }
        return StringUtils.isEmpty(lang) ?
                prefix + suffix :
                prefix + lang + suffix;
    }

}
