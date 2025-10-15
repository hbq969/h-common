package com.github.hbq969.code.common.java.ext.jsr;

import cn.hutool.core.lang.PatternPool;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class IPValidator implements ConstraintValidator<IP, String> {

    private IP ip;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value))
            return false;
        if (ip.batch()) {
            List<String> ipList = Splitter.on(ip.splitString()).omitEmptyStrings().splitToList(value);
            for (String ipStr : ipList) {
                if (!PatternPool.IPV4.matcher(ipStr).matches()
                        && !PatternPool.IPV6.matcher(ipStr).matches()) {
                    return false;
                }
            }
        } else {
            return PatternPool.IPV4.matcher(value).matches()
                    || PatternPool.IPV6.matcher(value).matches();
        }
        return true;
    }

    @Override
    public void initialize(IP ip) {
        this.ip = ip;
    }
}
