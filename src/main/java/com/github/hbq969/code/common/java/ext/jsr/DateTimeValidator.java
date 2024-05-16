package com.github.hbq969.code.common.java.ext.jsr;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.SimpleDateFormat;

/**
 * @author : hbq969@gmail.com
 * @description : 日志校验类
 * @createTime : 2023/8/30 11:40
 */
public class DateTimeValidator implements ConstraintValidator<DateTimeStr, String> {

  private DateTimeStr dateTimeStr;


  @Override
  public void initialize(DateTimeStr dateTimeStr) {
    this.dateTimeStr = dateTimeStr;
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    // 职责分离
    if (StringUtils.isEmpty(value)) {
      return true;
    }
    String format = dateTimeStr.format();

    if (value.length() != format.length()) {
      return false;
    }
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
    try {
      simpleDateFormat.parse(value);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
