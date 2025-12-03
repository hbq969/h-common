package com.github.hbq969.code.common.log.spi;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.log.model.PointModel;
import com.github.hbq969.code.common.log.utils.OperlogUtils;
import com.github.hbq969.code.common.spring.context.UserInfo;
import com.github.hbq969.code.common.utils.FormatTime;
import com.github.hbq969.code.common.utils.GsonUtils;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class DefaultLogCollect implements LogCollect {

    @SneakyThrows
    @Override
    public LogData collect(PointModel point) {
        ApiOperation op = AnnotationUtils.findAnnotation(point.getMethod(), ApiOperation.class);
        DefaultLogData data = null;
        if (op != null) {
            data = new DefaultLogData();
            String rid = MDC.get("requestId");
            data.setId(rid == null ? UUID.fastUUID().toString(true) : rid);

            ServletRequestAttributes holder = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            Assert.notNull(holder, "无法获取http请求上下文,请检查是否在非io线程中执行此代码");
            HttpServletRequest request = holder.getRequest();
            HttpSession session = request.getSession(false);
            if (session == null) {
                data.setUser(StringUtils.EMPTY);
            } else {
                Object user = session.getAttribute("h-sm-user");
                if (null == user) {
                    data.setUser(StringUtils.EMPTY);
                } else {
                    if (user instanceof UserInfo) {
                        UserInfo ui = (UserInfo) user;
                        data.setUser(ui.getUserName());
                    } else {
                        data.setUser(StringUtils.EMPTY);
                    }
                }
            }
            data.setTime(FormatTime.nowSecs());
            data.setUrl(request.getRequestURI());
            data.setMethodName(request.getMethod());
            data.setMethodDesc(OperlogUtils.getMethodDesc(point));
            data.setParameters(request.getQueryString());
            data.setBody(OperlogUtils.getPostBody(point));
            data.setResult(GsonUtils.toJson(point.getResult()));
        } else {
            data = collectByCustom(point);
        }
        return data;
    }

    /**
     * 扩展默认的自定义日志采集器逻辑
     *
     * @param point
     * @return
     */
    protected DefaultLogData collectByCustom(PointModel point) {
        DefaultLogData data = new DefaultLogData();
        String rid = MDC.get("requestId");
        data.setId(rid == null ? UUID.fastUUID().toString(true) : rid);
        ServletRequestAttributes holder = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (holder == null) {
            log.info("不在tomcat io线程中，无法获取会话信息");
            data.setUser("无会话信息");
            data.setUrl("不在tomcat io线程中,无法获取uri");
        } else {
            HttpServletRequest request = holder.getRequest();
            HttpSession session = request.getSession(false);
            Object user = session.getAttribute("h-sm-user");
            if (null == user) {
                data.setUser(StringUtils.EMPTY);
            } else {
                if (user instanceof UserInfo) {
                    UserInfo ui = (UserInfo) user;
                    data.setUser(ui.getUserName());
                } else {
                    data.setUser(StringUtils.EMPTY);
                }
            }
            data.setUrl(request.getRequestURI());
        }
        data.setTime(FormatTime.nowSecs());
        String mn = point.getTarget().getClass().getName() + "." + point.getMethod().getName();
        if (mn.length() > 50) {
            mn = mn.substring(mn.length() - 50);
        }
        data.setMethodName(mn);
        data.setMethodDesc(mn);
        Object[] paraNames = point.getParameterNames();
        Object[] paraValues = point.getParameterValues();
        if (ArrayUtil.isEmpty(paraNames)) {
            data.setParameters(null);
        } else {
            Map<String, Object> paraMap = new HashMap<>();
            for (int i = 0; i < paraNames.length; i++) {
                Object value = paraValues[i];
                // 过滤掉无法序列化的JDK内部类
                if (value != null && isSerializable(value)) {
                    paraMap.put(paraNames[i].toString(), value);
                } else {
                    paraMap.put(paraNames[i].toString(), value == null ? null : value.getClass().getSimpleName());
                }
            }
            data.setParameters(GsonUtils.toJson(paraMap));
        }
        data.setBody(null);
        Object result = point.getResult();
        if (result != null && isSerializable(result)) {
            data.setResult(GsonUtils.toJson(result));
        } else {
            data.setResult(result == null ? null : result.getClass().getSimpleName());
        }
        return data;
    }

    /**
     * 判断对象是否可以安全序列化
     */
    private boolean isSerializable(Object obj) {
        if (obj == null) {
            return true;
        }
        String className = obj.getClass().getName();
        // 排除JDK内部类和Servlet相关类
        return !className.startsWith("java.nio.")
                && !className.startsWith("java.lang.reflect.")
                && !className.startsWith("jakarta.servlet.")
                && !className.startsWith("javax.servlet.")
                && !className.startsWith("org.springframework.web.context.request.")
                && !className.startsWith("org.apache.catalina.")
                && !className.startsWith("org.apache.tomcat.");
    }
}
