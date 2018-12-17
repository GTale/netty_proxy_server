package com.phantom.netty.server.config;

import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class MyView extends FreeMarkerView {
    @Override
    protected void exposeHelpers(Map<String, Object> model, HttpServletRequest request) throws Exception {
        model.put("path", request.getContextPath());
        super.exposeHelpers(model, request);
    }
}