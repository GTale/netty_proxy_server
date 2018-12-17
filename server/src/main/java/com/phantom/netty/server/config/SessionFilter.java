package com.phantom.netty.server.config;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@WebFilter("/**")
public class SessionFilter implements Filter {

    private PathMatcher  pathMatcher;
    private List<String> excludes;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        pathMatcher = new AntPathMatcher();
        excludes = Arrays.asList(
                "/login.html",
                "/logout.html",

                // 放行静态文件
                "/static/**",
                "/lib/**",
                "/temp/**"
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest  request  = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession();
        if (session == null || session.getAttribute(Constant.userInfoField) == null) {

            // 获取当前访问的地址
            String uri         = request.getRequestURI();
            String contextPath = request.getContextPath();
            int    index       = uri.indexOf(contextPath);
            if (index != -1) {
                uri = uri.substring(index + contextPath.length());
            }

            if (match(uri)) {
                filterChain.doFilter(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private boolean match(String uri) {
        for (String exclude : excludes) {
            if (pathMatcher.match(exclude, uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {

    }
}
