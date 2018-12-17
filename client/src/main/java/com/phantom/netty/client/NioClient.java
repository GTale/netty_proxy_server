package com.phantom.netty.client;

import com.phantom.netty.client.console.LoginConsoleCommand;
import com.phantom.netty.client.entity.ClientConfig;
import com.phantom.netty.client.handler.*;
import com.phantom.netty.client.listener.CloseEvent;
import com.phantom.netty.client.util.AlertUtil;
import com.phantom.netty.client.util.BeanUtil;
import com.phantom.netty.client.util.GuiState;
import com.phantom.netty.client.util.NioEventUtil;
import com.phantom.netty.client.view.ProgressFrom;
import com.phantom.netty.common.protocol.codec.HttpRequestMessageEncoder;
import com.phantom.netty.common.protocol.codec.HttpResponseMessageEncoder;
import com.phantom.netty.common.protocol.codec.MessageCodec;
import com.phantom.netty.common.protocol.common.NettyIdleStateHandler;
import com.phantom.netty.common.protocol.common.Spliter;
import com.phantom.netty.common.util.ssl.TlsUtil;
import de.felixroske.jfxsupport.GUIState;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import javafx.application.Platform;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author: phantom
 * @Date: 2018/11/28 10:22
 * @Description:
 */
@Slf4j
public class NioClient {

    private static final int               MAX_RETRY          = 5;
    private static final int               MAX_CONTENT_LENGTH = 20 * 1024 * 1024;
    private static       NioEventLoopGroup work;

    public static Bootstrap getBootstrap(String host, int port) {
        // 配置连接真正服务器的Bootstrap
        Bootstrap realServerBootstrap = buildRealConnectionBootstrap();
        work = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(work)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // tls
                        socketChannel.pipeline().addLast(TlsUtil.forClient().newHandler(socketChannel.alloc(), host, port));
                        // 空闲检测,服务器超时,断开连接
                        socketChannel.pipeline().addLast(new NettyIdleStateHandler(reConnect));
                        // 粘包
                        socketChannel.pipeline().addLast(new Spliter());
                        // 解码
                        socketChannel.pipeline().addLast(MessageCodec.INSTANCE);
                        // 登陆
                        socketChannel.pipeline().addLast(LoginResponseHandler.INSTANCE);
                        // 定时发送心跳包
                        socketChannel.pipeline().addLast(new HeartBeatTimerHandler());
                        // 接收远程过来的代理数据包
                        socketChannel.pipeline().addLast(new DispatcherRequestHandler(realServerBootstrap, new HttpResponseMessageEncoder()));
                        // 解析Http数据包
                        socketChannel.pipeline().addLast(new HttpRequestDecoder());
                        // 聚合http数据包
                        socketChannel.pipeline().addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                        // 处理http数据包
                        socketChannel.pipeline().addLast(new HttpRequestHandler(new HttpRequestMessageEncoder()));
                    }
                });
        return bootstrap;
    }

    /**
     * 同步连接
     */
    public static Channel syncConnect(Bootstrap bootstrap, String host, int port, int retry) {
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {
                NioEventUtil.add(work);
                return future.channel();
            } else if (retry == 0) {
                return null;
            } else {
                int order = (MAX_RETRY - retry) + 1;
                int delay = 1 << order;
                TimeUnit.SECONDS.sleep(delay);
                syncConnect(bootstrap, host, port, retry - 1);
            }
        } catch (Exception e) {
            log.error("服务器{}:{},连接失败", host, port);
        }
        return null;
    }

    /**
     * 连接服务器
     */
    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("{}:{} 连接成功", host, port);
                Channel channel = ((ChannelFuture) future).channel();
            } else if (retry == 0) {
                log.error("重试次数已用完，放弃连接");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;
                // 本次重连的间隔
                int delay = 1 << order;
                System.err.println(new Date() + ": 连接失败，第" + order + "次重连……");
                bootstrap.config().group().schedule(() -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit
                        .SECONDS);
            }
        });
    }

    /**
     * 用于连接真正的服务器
     */
    private static Bootstrap buildRealConnectionBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new TcpHandler());
                        ch.pipeline().addLast(new HttpResponseDecoder());
                        ch.pipeline().addLast(new RealServerMessageHandler(new HttpResponseMessageEncoder()));
                    }
                });
        return bootstrap;
    }

    private static Supplier reConnect = () -> {
        Platform.runLater(() -> {
            GuiState.getServerStatus().setText("连接断开");
            AlertUtil.showConfirmAlert("与服务器的连接已断开,是否尝试重新连接?", () -> {
                ProgressFrom progress = new ProgressFrom(new Task<Object>() {
                    @Override
                    protected Object call() throws Exception {
                        NioEventUtil.close();
                        ClientConfig config    = GuiState.getConfig();
                        Bootstrap    bootstrap = NioClient.getBootstrap(config.getServerUrl(), config.getPort());
                        Channel      channel   = NioClient.syncConnect(bootstrap, config.getServerUrl(), config.getPort(), config.getMaxRetry());
                        if (channel != null) {
                            // 发出登陆请求
                            boolean result = LoginConsoleCommand.login(channel, config.getUsername(), config.getPassword());
                            if (result) {
                                GuiState.getServerStatus().setText("连接成功");

                                Platform.runLater(() ->
                                        AlertUtil.showInfoAlert("重新连接成功")
                                );
                                return 0;
                            }
                        }

                        Platform.runLater(() ->
                                AlertUtil.showErrorAlert("重新连接失败,即将退出系统", () -> {
                                    BeanUtil.getCtx().publishEvent(new CloseEvent(""));
                                    return null;
                                })
                        );
                        return 0;
                    }
                }, GUIState.getStage());
                progress.activateProgressBar();
                return null;
            }, () -> {
                BeanUtil.getCtx().publishEvent(new CloseEvent(""));
                return null;
            });
        });
        return null;
    };
}
