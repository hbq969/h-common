package com.github.hbq969.code.common.java.ext.jsr;

import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : hbq969@gmail.com
 * @description : 字典校验器
 * @createTime : 2023/8/30 13:34
 */
public class DictValidator implements ConstraintValidator<Dict, Object> {

    private Dict dict;

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Set<String> set = Splitter.on(Pattern.compile("[,;]")).splitToList(dict.expectValue()).stream()
                .map(v -> v.toString()).collect(Collectors.toSet());
        if (StringUtils.isEmpty(dict.expectValue())) {
            return false;
        }
        if (value instanceof Number || value instanceof Boolean) {
            if (!set.contains(String.valueOf(value))) {
                return false;
            }
            return true;
        } else if (value instanceof String) {
            String valueStr = value.toString();
            if (valueStr.contains(",") || valueStr.contains(";")) {
                List<String> list = Splitter.on(Pattern.compile("[,;]")).splitToList(valueStr);
                for (String singleDictValue : list) {
                    if (!set.contains(singleDictValue)) {
                        return false;
                    }
                }
            } else if (!set.contains(valueStr)) {
                return false;
            }
            return true;
        } else if (value instanceof List) {
            List list = (List) value;
            for (Object singleDictValue : list) {
                if (!set.contains(String.valueOf(singleDictValue))) {
                    return false;
                }
            }
            return true;
        } else {
            throw new UnsupportedOperationException(
                    MessageFormat.format("不支持的校验类型 {0}", value.getClass()));
        }
    }

    @Override
    public void initialize(Dict dict) {
        this.dict = dict;
    }
}
