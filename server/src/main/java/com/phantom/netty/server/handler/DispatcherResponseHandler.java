package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.packet.impl.DispatcherResponsePacket;
import com.phantom.netty.common.util.session.SessionUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: phantom
 * @Date: 2018/11/30 08:52
 * @Description:
 */
public class DispatcherResponseHandler extends SimpleChannelInboundHandler<DispatcherResponsePacket> {

    public static DispatcherResponseHandler INSTANCE = new DispatcherResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                DispatcherResponsePacket dispatcherResponsePacket) throws Exception {
        // 转发数据给真实用户
        Channel channel = SessionUtil.getSocketChannel(dispatcherResponsePacket.getSequenceId());
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(Unpooled.wrappedBuffer((byte[]) dispatcherResponsePacket.getMsg()));
        }
    }
}
