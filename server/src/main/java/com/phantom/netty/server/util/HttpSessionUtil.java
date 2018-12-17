package com.phantom.netty.server.util;

import com.phantom.netty.server.config.Constant;
import com.phantom.netty.server.entity.UserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class HttpSessionUtil {
    public static UserInfo getUserInfo(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            return (UserInfo) session.getAttribute(Constant.userInfoField);
        } else {
            return null;
        }
    }
}
