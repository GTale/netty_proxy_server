package com.phantom.netty.server.util.server;

import com.phantom.netty.server.util.ServerHelper;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * 注销已经开启的服务
 */
@Component
public class CloseServer implements DisposableBean {
    @Override
    public void destroy() {
        ServerHelper.getGroups().forEach((k, v) ->
                v.forEach(gc -> {
                    if (gc instanceof NioEventLoopGroup) {
                        ((NioEventLoopGroup) gc).shutdownGracefully();
                    } else if (gc instanceof NioEventLoop) {
                        ((NioEventLoop) gc).shutdownGracefully();
                    }
                })
        );
    }
}
