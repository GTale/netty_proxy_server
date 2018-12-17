package com.phantom.netty.client.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanUtil implements ApplicationContextAware {

    @Getter
    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> T getBean(Class<T> cls, String beanName) {
        return ctx.getBean(cls, beanName);
    }

    public static <T> T getBean(Class<T> cls) {
        return ctx.getBean(cls);
    }

    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }
}
