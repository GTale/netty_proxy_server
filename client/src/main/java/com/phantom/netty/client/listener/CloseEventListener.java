package com.phantom.netty.client.listener;

import com.phantom.netty.client.util.NioEventUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CloseEventListener implements ApplicationListener<CloseEvent> {
    @Override
    public void onApplicationEvent(CloseEvent closeEvent) {
        NioEventUtil.close();
        System.exit(0);
    }
}
