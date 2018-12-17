package com.phantom.netty.server.config;

import freemarker.template.TemplateModelException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class Config {

    @Bean
    public Object object(FreeMarkerViewResolver resolver) throws TemplateModelException {
        resolver.setViewClass(MyView.class);
        return null;
    }

}
