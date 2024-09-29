package com.github.hbq969.code.common.spring.advice.ex;

import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.restful.ReturnState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * @author hbq969@gmail.com
 * @date 2024/09/20 20:06
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ReturnMessage<?> messageExceptionHandler(HttpMessageNotReadableException e) {
        log.info("", e);
        ReturnMessage<?> result = new ReturnMessage<>();
        result.setState(ReturnState.ERROR);
        result.setErrorMessage("http序列化错误");
        return result;
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String errorMessage = String.join("，", ex.getPropertyName(),
                "传参类型不准确", " 定义的类型" + ex.getParameter().getParameterType());
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(BindException.class)
    public ReturnMessage<?> handleValidationException(BindingResult result) {
        List<FieldError> errs = result.getFieldErrors();
        FieldError err = errs.get(0);
        return ReturnMessage.fail(String.join(":", err.getField(), err.getDefaultMessage()));
    }

    @ExceptionHandler({BizException.class, RuntimeException.class})
    public ReturnMessage<?> handleBizException(RuntimeException ex) {
        log.error("", ex);
        if (ex instanceof BizException) {
            BizException bex = (BizException) ex;
            return ReturnMessage.fail(bex.getErrorCode(), bex.getErrorMessage());
        } else {
            return ReturnMessage.fail(ex.getMessage());
        }
    }
}
