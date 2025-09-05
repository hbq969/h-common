package com.github.hbq969.code.common.log.spi;

import cn.hutool.core.lang.UUID;
import com.github.hbq969.code.common.log.model.PointModel;
import com.github.hbq969.code.common.log.utils.OperlogUtils;
import com.github.hbq969.code.common.spring.context.UserInfo;
import com.github.hbq969.code.common.utils.FormatTime;
import com.github.hbq969.code.common.utils.GsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Slf4j
public class DefaultLogCollect implements LogCollect {

    @SneakyThrows
    @Override
    public LogData collect(PointModel point) {
        DefaultLogData data = new DefaultLogData();
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
        return data;
    }
}
