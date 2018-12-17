package com.phantom.netty.client.handler;

import com.phantom.netty.client.util.ClientSessionUtil;
import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.protocol.packet.impl.DispatcherResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: phantom
 * @Date: 2018/12/7 17:01
 * @Description:
 */
@Slf4j
public class TcpHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //判断当前的channel是tcp还是http
        Channel realServerChannel = ctx.channel();
        byte proxyType = ClientSessionUtil.getProxyType(realServerChannel);
        if (proxyType == CommonType.ProxyType.HTTP) {
            ctx.fireChannelRead(msg);
        } else {
            Channel channel = ClientSessionUtil.getChannel();
            // 读取数据
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            ReferenceCountUtil.release(byteBuf);

            // 发送到服务端
            if (channel != null && channel.isActive()) {
//                log.info("[Server==>Proxy],转发[TCP]数据到代理服务器");
                DispatcherResponsePacket responsePacket = new DispatcherResponsePacket();
                responsePacket.setSequenceId(ClientSessionUtil.getSequence(realServerChannel));
                responsePacket.setMsg(bytes);
                channel.writeAndFlush(responsePacket);
            } else {
                log.error("[Proxy<==>Server],连接已经断开");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
                                Throwable cause) throws Exception {
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().close();
    }
}
