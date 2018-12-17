package com.phantom.netty.server.util.server;

import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.util.session.Attributes;
import com.phantom.netty.server.entity.ConnectStatus;
import com.phantom.netty.server.entity.ProxyServer;
import com.phantom.netty.server.handler.ForwardDataHandler;
import com.phantom.netty.server.handler.HttpConnectionHandler;
import com.phantom.netty.server.handler.HttpMessageHandler;
import com.phantom.netty.server.util.ServerHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author: phantom
 * @Date: 2018/11/30 09:06
 * @Description: 远程服务, 暴露给外网连接
 */
@Slf4j
public class RemoteServer {

    public static void main(String[] args) {
        NettyServer.start(8000);
        ProxyServer server = new ProxyServer();
        server.setPort(5554);
        server.setServerToken("11");
        server.setProxyType(CommonType.ProxyType.HTTP);
        buildServer(server);
    }

    // 1.创建主接收线程池
    private static NioEventLoopGroup parent = new NioEventLoopGroup();

    /**
     * 创建服务
     */
    public static void buildServer(ProxyServer server) {
        // 1.数据处理线程
        NioEventLoopGroup worker = new NioEventLoopGroup();

        // 2.创建监听
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(parent, worker)
                .channel(NioServerSocketChannel.class)
                .childAttr(Attributes.USER_TOKEN, server.getUserToken());

        switch (server.getProxyType()) {
            case CommonType.ProxyType.TCP:
                buildTcpServer(bootstrap, server);
                break;
            case CommonType.ProxyType.HTTP:
                buildHttpServer(bootstrap, server);
                break;
        }

        // 3.绑定端口
        int port = server.getPort();
        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("监听服务启动成功,正在监听:{}", port);
            ServerHelper.bindGroup(server.getServerToken(), Arrays.asList(channelFuture.channel().eventLoop(), worker));
            server.setConnectStatus(ConnectStatus.ONLINE);
        } catch (Exception e) {
            String msg = "监听服务启动失败," + port + "端口冲突";
            server.setConnectStatus(ConnectStatus.CONNECT_FAIL);
            server.setConnectMsg(msg);
            log.error(msg);
        }
    }

    /**
     * 建立tcp监听
     */
    public static void buildTcpServer(ServerBootstrap bootstrap, ProxyServer server) {
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new ForwardDataHandler(server.getRemoteHost(), server.getRemotePort(), server.getProxyType()));
            }
        });
    }

    /**
     * 建立Http监听
     */
    public static void buildHttpServer(ServerBootstrap bootstrap, ProxyServer server) {
        int MAX_CONTENT_LENGTH = 2 * 1024 * 1024;
        bootstrap
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(CommonType.HTTP_REQUEST_DECODER, new HttpRequestDecoder());
                        ch.pipeline().addLast(CommonType.HTTP_AGGREGATOR, new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                        ch.pipeline().addLast(new HttpConnectionHandler());
                        ch.pipeline().addLast(new HttpMessageHandler());
                    }
                });
    }

    /**
     * 关闭监听服务
     */
    public static void closeServer(ProxyServer server) {
        String serverToken = server.getServerToken();
        log.info("正在关闭{}端口", server.getPort());
        Optional.ofNullable(ServerHelper.removeGroup(serverToken)).ifPresent(g ->
                g.forEach(gc -> {
                    if (gc instanceof NioEventLoopGroup) {
                        ((NioEventLoopGroup) gc).shutdownGracefully();
                    } else if (gc instanceof NioEventLoop) {
                        ((NioEventLoop) gc).shutdownGracefully();
                    }
                })
        );
    }
}
