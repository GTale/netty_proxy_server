package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.protocol.packet.impl.DispatcherRequestPacket;
import com.phantom.netty.common.util.TaskPool;
import com.phantom.netty.common.util.session.SessionUtil;
import com.phantom.netty.server.util.UserTokenUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author: phantom
 * @Date: 2018/11/30 10:12
 * @Description: 本地端口之间进行信息转发
 */
public class ForwardDataHandler extends ChannelInboundHandlerAdapter {
    private String sequenceId;
    private String host;
    private int    port;
    private byte   proxyType;

    public ForwardDataHandler(String host, int port, byte proxyType) {
        this.host = host;
        this.port = port;
        this.proxyType = proxyType;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 1.发送指令给客户端,让客户端建立本地连接
        sequenceId = ctx.channel().id().asShortText();
        DispatcherRequestPacket packet = new DispatcherRequestPacket();
        packet.setSequenceId(sequenceId);
        packet.setMessageType(CommonType.MessageType.TYPE_CONNECT_REAL_SERVER);
        packet.setProxyType(proxyType);
        packet.setHost(host);
        packet.setPort(port);
        packet.setUserToken(UserTokenUtil.getUserToken(ctx.channel()));

        // 2.添加到消息队列
        TaskPool.addTask(packet);

        // 3.绑定参数
        ctx.channel().config().setAutoRead(false);
        SessionUtil.bindSocketChannel(sequenceId, ctx.channel());
    }

    /**
     * 转发消息给远程客户端,如果有消息,继续下发到指令端口
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 1.读取数据
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[]  bytes   = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        ReferenceCountUtil.release(byteBuf);

        // 2.包整合
        DispatcherRequestPacket packet = new DispatcherRequestPacket();
        packet.setSequenceId(sequenceId);
        packet.setHost(host);
        packet.setPort(port);
        packet.setMessageType(CommonType.MessageType.TYPE_MESSAGE);
        packet.setProxyType(proxyType);
        packet.setMsg(bytes);
        packet.setUserToken(UserTokenUtil.getUserToken(ctx.channel()));

        // 3.下发数据包
        TaskPool.addTask(packet);
    }
}
