package com.github.hbq969.code.common.spring.context;

import com.github.hbq969.code.common.utils.GsonUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author hbq
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = -1;

    private String userName;

    public UserInfo() {
    }

    public UserInfo(String userName) {
        this.userName = userName;
    }

    public static UserInfo of(String userInfo) {

        return null == userInfo ? new UserInfo()
                : GsonUtils.fromJson(userInfo, UserInfo.class);
    }

    public static <T extends UserInfo> T of(Class<T> c) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attrs.getRequest();
        HttpSession session = request.getSession();
        if (session == null) {
            throw new RuntimeException("无会话信息，请先登录");
        }
        Object user = session.getAttribute("h-sm-user");
        if (user == null) {
            throw new RuntimeException("会话已失效，请先登录");
        }
        return GsonUtils.fromJson(user.toString(), c);
    }

    public String getDefaultUserName(String defaultValue) {
        return Optional.ofNullable(userName).orElse(defaultValue);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
