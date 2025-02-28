package com.github.hbq969.code.common.spring.advice.rest;

import cn.hutool.core.lang.Validator;
import com.github.hbq969.code.common.restful.ReturnMessage;
import com.github.hbq969.code.common.spring.advice.handler.RestfulHandler;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Slf4j
public class RestfulAdvice implements InitializingBean {

    @Value("${advice.health-check.methods:health}")
    private Set<String> healthCheckMethods;

    @Autowired
    private SpringContext context;

    private List<RestfulHandler> handlers;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, RestfulHandler> map = context.getBeanMapOfType(RestfulHandler.class);
        if (MapUtils.isNotEmpty(map)) {
            handlers = map.values().stream()
                    .sorted(Comparator.comparing(RestfulHandler::order))
                    .collect(Collectors.toList());
        }
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.GetMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.PostMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.PutMapping) "
            + "|| @annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void restfulPointCut() {
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Around("restfulPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        RestController rc = AnnotationUtils.findAnnotation(point.getTarget().getClass(), RestController.class);
        // 非RestControl类不拦截s
        if (rc == null) {
            return point.proceed();
        }
        MethodSignature ms = (MethodSignature) point.getSignature();
        if (healthCheckMethods.contains(ms.getName())) {
            return point.proceed();
        }
        if (handlers == null) {
            return point.proceed();
        }
        try {
            for (RestfulHandler handler : handlers) {
                handler.before(point);
            }
            Object result = point.proceed();
            for (RestfulHandler handler : handlers) {
                handler.after(point, result);
            }
            return result;
        } catch (Throwable ex) {
            for (RestfulHandler handler : handlers) {
                handler.exception(point, ex);
            }
            String errMsg = ex.getMessage();
            if (errMsg == null) {
                return ReturnMessage.fail("后端接口异常");
            }
            return Validator.hasChinese(errMsg) ?
                    ReturnMessage.fail(errMsg) :
                    ReturnMessage.fail("后端接口异常");
        }
    }
}
