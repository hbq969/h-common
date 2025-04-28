package com.github.hbq969.code.common.spring.advice.log;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.utils.FormatTime;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

/**
 * @author : hbq969@gmail.com
 * @description : restful接口日志记录
 * @createTime : 2023/8/27 12:19
 */
@Aspect
@Slf4j
public class LogAdvice {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) " + "|| @annotation(org.springframework.web.bind.annotation.GetMapping) " + "|| @annotation(org.springframework.web.bind.annotation.PostMapping) " + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) " + "|| @annotation(org.springframework.web.bind.annotation.PutMapping) " + "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void restfulLogPointCut() {
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Around("restfulLogPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        RestController rc = AnnotationUtils.findAnnotation(point.getTarget().getClass(), RestController.class);
        // 非RestControl类不拦截s
        if (rc == null) {
            return point.proceed();
        }
        MethodSignature ms = (MethodSignature) point.getSignature();
        if (ms.getName().equals("health")) {
            return point.proceed();
        }
        long t = FormatTime.nowMills();
        try {
            if (ArrayUtil.isNotEmpty(point.getArgs())) {
                StringBuilder sb = new StringBuilder(200);
                int len = point.getArgs().length;
                for (int i = 0; i < len; i++) {
                    sb.append(", ").append(ms.getParameterNames()[i]).append("=").append(point.getArgs()[i]);
                }
                log.info("调用开始, [{}], <{}>", ms.getName(), sb.length() > 0 ? sb.substring(2) : "");
            } else {
                log.info("调用开始, [{}], <>", ms.getName());
            }
            Object result = point.proceed();
            t = FormatTime.nowMills() - t;
            log.info("调用结束, [{}], 结果: {}, 耗时: {} ms", ms.getName(), result, t);
            return result;
        } catch (Throwable ex) {
            log.error(MessageFormat.format("调用接口[{0}]异常", ms.getName()), ex);
            t = FormatTime.nowMills() - t;
            log.info("调用异常, [{}], 耗时: {} ms", ms.getName(), t);
            String errMsg = ex.getMessage();
            if (errMsg == null) {
                return ReturnMessage.fail("后端接口异常");
            }
            return ReturnMessage.fail(errMsg);
        }
    }

}
