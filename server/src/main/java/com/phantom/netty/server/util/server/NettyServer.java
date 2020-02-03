package com.phantom.netty.server.util.server;


import com.phantom.netty.common.protocol.codec.MessageCodec;
import com.phantom.netty.common.protocol.common.NettyIdleStateHandler;
import com.phantom.netty.common.protocol.common.Spliter;
import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.util.IdUtil;
import com.phantom.netty.common.util.TaskPool;
import com.phantom.netty.common.util.session.Session;
import com.phantom.netty.common.util.session.SessionUtil;
import com.phantom.netty.common.util.ssl.SslConfig;
import com.phantom.netty.common.util.ssl.TlsUtil;
import com.phantom.netty.server.handler.AuthHandler;
import com.phantom.netty.server.handler.CommonHandler;
import com.phantom.netty.server.handler.HeartBeatRequestHandler;
import com.phantom.netty.server.handler.LoginRequestHandler;
import com.phantom.netty.server.util.ServerHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;
import java.util.Arrays;

@Slf4j
public class NettyServer {
    private static SslConfig config;

    static {
        config = new SslConfig();
        config.setCertFilePath("tls/ca.crt");
        config.setKeyFilePath("tls/ca.pem");
    }

    public static void start(int port) {
        // 1.创建两个组
        NioEventLoopGroup parent = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();

        // 2.创建监听
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(parent, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 开启https请求
                        SSLEngine sslEngine = TlsUtil.forService(config).newEngine(ch.alloc());
                        sslEngine.setUseClientMode(false);
                        sslEngine.setNeedClientAuth(false);
                        ch.pipeline().addLast(new SslHandler(sslEngine));
                        // 空闲检测
                        ch.pipeline().addLast(new NettyIdleStateHandler(null));
                        // 分包
                        ch.pipeline().addLast(new Spliter());
                        // 解码
                        ch.pipeline().addLast(MessageCodec.INSTANCE);
                        // 登陆
                        ch.pipeline().addLast(LoginRequestHandler.INSTANCE);
                        // 接收,并返回心跳
                        ch.pipeline().addLast(HeartBeatRequestHandler.INSTANCE);
                        // 权限认证
                        ch.pipeline().addLast(AuthHandler.INSTANCE);
                        // 通用处理
                        ch.pipeline().addLast(CommonHandler.INSTANCE);
                        // 添加定时任务
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                            private Thread t;

                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                log.info("有客户端连接：{}", port);

                                // 发送消息到客户端
                                t = new Thread(() -> {
                                    while (true) {
                                        if (ctx.channel().isActive()) {
                                            Session session = SessionUtil.getSession(ctx.channel());
                                            if (session != null) {
                                                Packet packet = TaskPool.get(session.getUserId());
                                                if (packet != null) {
                                                    ctx.channel().writeAndFlush(packet);
                                                }
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                });
                                t.start();
                                super.channelActive(ctx);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                // 记录断开的用户
                                Session session = SessionUtil.getSession(ctx.channel());
                                SessionUtil.addLogoutUser(session.getUserId());
                                super.channelInactive(ctx);
                            }
                        });
                    }
                });

        // 3.绑定端口
        bootstrap.bind(port).addListener((future) -> {
            if (future.isSuccess()) {
                log.info("服务端启动成功,正在监听:" + port);
                // 启动成功,绑定到主要
                ServerHelper.bindGroup(IdUtil.getId(), Arrays.asList(parent, worker));
            } else {
                log.error("服务启动失败," + port + "端口冲突");
            }
        });
    }
}
