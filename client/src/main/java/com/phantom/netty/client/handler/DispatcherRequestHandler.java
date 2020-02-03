package com.phantom.netty.client.handler;

import com.phantom.netty.client.util.ClientSessionUtil;
import com.phantom.netty.client.util.GuiState;
import com.phantom.netty.common.protocol.codec.HttpResponseMessageEncoder;
import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.protocol.packet.impl.ChannelBuildResponsePacket;
import com.phantom.netty.common.protocol.packet.impl.DispatcherRequestPacket;
import com.phantom.netty.common.util.session.Attributes;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author: phantom
 * @Date: 2018/11/30 08:57
 * @Description: 接受服务器发送过来的指定和数据，
 */
@Slf4j
public class DispatcherRequestHandler extends SimpleChannelInboundHandler<DispatcherRequestPacket> {

    private Bootstrap                  bootstrap;
    private HttpResponseMessageEncoder encoder;

    public DispatcherRequestHandler(Bootstrap bootstrap, HttpResponseMessageEncoder encoder) {
        this.bootstrap = bootstrap;
        this.encoder = encoder;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DispatcherRequestPacket packet) {
        // 分析当前信息类型
        byte messageType = packet.getMessageType();
        switch (messageType) {
            case CommonType.MessageType.TYPE_CONNECT_REAL_SERVER:
                //处理连接事件
                GuiState.log("[Client==>Proxy]," + packet.getHost() + ":" + packet.getPort());
                handleConnections(ctx, packet);
                break;
            case CommonType.MessageType.TYPE_MESSAGE:
                if ((packet.getProxyType() & 0xff) == CommonType.ProxyType.TCP) {
                    handleMessage(packet);
                } else if ((packet.getProxyType() & 0xff) == CommonType.ProxyType.HTTP || (packet.getProxyType() & 0xff) == CommonType.ProxyType.TUNNEL) {
                    handleHttpMessage(ctx, packet);
                } else {
                    GuiState.log("消息处理失败,ProxyType错误：" + packet.getProxyType());
                }
                break;
            default:
                GuiState.log("消息处理失败,MessageType错误：" + messageType);
                break;
        }
    }

    /**
     * 处理连接事件。
     * 一个两个情况：
     * 1)如果是普通的http或者tcp的连接，通过Client与指定服务建立连接后，只需要发送一个应答包给Server就行了。
     * 2)如果是Tunnel(一般是https)的情况,通过Client与指定服务建立连接后，还需要在应答包里面添加一个普通的200响应包，表示我们是作为转发。
     */
    private void handleConnections(ChannelHandlerContext ctx, DispatcherRequestPacket packet) {

        String host      = packet.getHost();
        int    port      = packet.getPort();
        byte   proxyType = packet.getProxyType();
        String sequence  = packet.getSequenceId();

        bootstrap.connect(host, port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
//                log.info("[Proxy<==>Server],连接建立成功");

                // 1.绑定当前连接到缓存
                Channel channel = future.channel();
                channel.attr(Attributes.PROXY_TYPE).set(proxyType);
                ClientSessionUtil.bindSocketChannel(sequence, channel);

                // 判断当前的连接类型是否是https的连接
                if (Objects.equals(proxyType, CommonType.ProxyType.TUNNEL)) {
                    // 简单的回响给用户,表示当前只做转发
                    HttpVersion      version  = HttpVersion.valueOf((String) packet.getMsg());
                    FullHttpResponse response = new DefaultFullHttpResponse(version, HttpResponseStatus.OK);
                    // 封装消息
                    List<Object> output = new ArrayList<>();
                    encoder.encode(ctx, response, output);
                    output.forEach(o -> {
                        // 读取数据
                        ByteBuf byteBuf = (ByteBuf) o;
                        byte[]  bytes   = new byte[byteBuf.readableBytes()];
                        byteBuf.readBytes(bytes);
                        ReferenceCountUtil.release(byteBuf);

                        ChannelBuildResponsePacket buildPacket = new ChannelBuildResponsePacket();
                        buildPacket.setSequenceId(sequence);
                        buildPacket.setProxyType(proxyType);
                        buildPacket.setHost(host);
                        buildPacket.setPort(port);
                        buildPacket.setMsg(bytes);
                        ClientSessionUtil.getChannel().writeAndFlush(buildPacket);
                    });
                } else {
                    // http与tcp的连接建立成功后,直接通知服务器,通过已经建立完毕,可以继续下发指令
                    ChannelBuildResponsePacket buildPacket = new ChannelBuildResponsePacket();
                    buildPacket.setSequenceId(sequence);
                    buildPacket.setProxyType(proxyType);
                    buildPacket.setHost(host);
                    buildPacket.setPort(port);
                    ClientSessionUtil.getChannel().writeAndFlush(buildPacket);
                }
            } else {
                log.error("[Proxy<==>Server],连接失败,{}:{}", host, port);
            }
        });
    }

    /**
     * 处理消息事件
     */
    private void handleMessage(DispatcherRequestPacket packet) {
        // 获取当前连接的channel
        Channel channel = ClientSessionUtil.getSocketChannel(packet.getSequenceId());

        if (channel == null) {
            log.info("[Proxy==>Server],发送失败,尚未建立连接");
        }

        GuiState.log("[Proxy==>Server]," + packet.getHost() + ":" + packet.getPort());
        sendPacket(channel, (byte[]) packet.getMsg());
    }

    /**
     * 处理http消息
     */
    private void handleHttpMessage(ChannelHandlerContext ctx, DispatcherRequestPacket packet) {
        Optional.ofNullable(packet.getMsg()).ifPresent(msg -> {
            if (packet.getProxyType() == CommonType.ProxyType.TUNNEL) {
                // 直接转发https消息
                Channel realServerChannel = ClientSessionUtil.getSocketChannel(packet.getSequenceId());
                if (realServerChannel != null && realServerChannel.isActive()) {
                    realServerChannel.writeAndFlush(Unpooled.wrappedBuffer((byte[]) packet.getMsg()));
                }
            } else {
                ctx.fireChannelRead(Unpooled.wrappedBuffer((byte[]) msg));
            }
        });
    }

    /**
     * 发送消息
     */
    private void sendPacket(Channel channel, byte[] bytes) {
        if (bytes != null) {
            channel.writeAndFlush(Unpooled.wrappedBuffer(bytes));
        }
    }
}
