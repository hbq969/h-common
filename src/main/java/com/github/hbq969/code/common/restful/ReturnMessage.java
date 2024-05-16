package com.github.hbq969.code.common.restful;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.hbq969.code.common.utils.UuidUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Arrays;

/**
 * @author : hbq969@gmail.com
 * @description : API返回结果对象
 * @createTime : 2023/6/26 14:47
 */
public class ReturnMessage<T> {

  private ReturnState state;
  private T body;
  @JsonInclude(Include.NON_EMPTY)
  private String errorCode;
  @JsonInclude(Include.NON_EMPTY)
  private String[] errorParams;
  @JsonInclude(Include.NON_EMPTY)
  private String errorMessage;
  private String requestId;

  public ReturnMessage() {
    this.requestId =
        StringUtils.isNotBlank(MDC.get("requestId")) ? MDC.get("requestId") : UuidUtil.uuid();
  }

  public ReturnMessage(ReturnState state) {
    this();
    this.state = state;
  }

  public ReturnMessage(ReturnState state, T body) {
    this();
    this.state = state;
    if (ReturnState.OK != state) {
      this.errorCode = (String) body;
    } else {
      this.body = body;
    }

  }

  public ReturnMessage(ReturnState state, String errorCode, String... errorParams) {
    this();
    this.state = state;
    this.errorCode = errorCode;
    this.errorParams = errorParams;
  }

  public static ReturnMessage fail() {
    ReturnMessage result = new ReturnMessage();
    result.setState(ReturnState.ERROR);
    return result;
  }

  public static ReturnMessage fail(String errorMessage) {
    ReturnMessage result = new ReturnMessage();
    result.setState(ReturnState.ERROR);
    result.setErrorMessage(errorMessage);
    return result;
  }

  public static ReturnMessage fail(String errorCode, String errorMessage) {
    ReturnMessage result = new ReturnMessage();
    result.setState(ReturnState.ERROR);
    result.setErrorCode(errorCode);
    result.setErrorMessage(errorMessage);
    return result;
  }

  public static ReturnMessage success(Object body) {
    ReturnMessage result = new ReturnMessage();
    result.setState(ReturnState.OK);
    result.setBody(body);
    return result;
  }

  public String toString() {
    return "ReturnMessage(state=" + this.getState() + ", errorCode=" + this.getErrorCode()
        + ", errorParams=" + Arrays.deepToString(this.getErrorParams()) + ", errorMessage="
        + this.getErrorMessage() + ", requestId=" + this.getRequestId() + ")";
  }

  public ReturnState getState() {
    return this.state;
  }

  public void setState(ReturnState state) {
    this.state = state;
  }

  public T getBody() {
    return this.body;
  }

  public void setBody(T body) {
    this.body = body;
  }

  public String getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  public String[] getErrorParams() {
    return this.errorParams;
  }

  public void setErrorParams(String[] errorParams) {
    this.errorParams = errorParams;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getRequestId() {
    return this.requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public boolean ifSuccess() {
    return state != null && state.ok();
  }

  public boolean ifFail() {
    return !ifSuccess();
  }
}
