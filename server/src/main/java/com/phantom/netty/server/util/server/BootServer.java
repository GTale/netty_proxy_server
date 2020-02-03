package com.phantom.netty.server.util.server;

import com.phantom.netty.server.config.ProxyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author: phantom
 * @Date: 2018/12/6 09:47
 * @Description: 实现ApplicationRunner接口，随程序自动启动
 */
@Component
public class BootServer implements ApplicationRunner {

    @Autowired
    private ProxyConfig config;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 启动netty监听指令
        NettyServer.start(config.getPort());
    }
}
