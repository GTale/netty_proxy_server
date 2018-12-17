package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.protocol.packet.impl.ChannelBuildResponsePacket;
import com.phantom.netty.common.util.session.SessionUtil;
import com.phantom.netty.server.event.TransferEvent;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: phantom
 * @Date: 2018/12/3 14:37
 * @Description:
 */
@Slf4j
public class ChannelBuildResponseHandler extends SimpleChannelInboundHandler<ChannelBuildResponsePacket> {

    public static ChannelBuildResponseHandler INSTANCE = new ChannelBuildResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ChannelBuildResponsePacket channelBuildPacket) throws Exception {
        // 设置用户数据可读
        Channel userChannel = SessionUtil.getSocketChannel(channelBuildPacket.getSequenceId());
        if (userChannel != null && userChannel.isActive()) {
            // 判断当前的消息类型
            byte proxyType = channelBuildPacket.getProxyType();
            if (proxyType == CommonType.ProxyType.HTTP) {
                // http消息,进行广播
                userChannel.pipeline().fireUserEventTriggered(new TransferEvent());
            } else if (proxyType == CommonType.ProxyType.TUNNEL) {
                // tunnel消息,进行简单的回响
                userChannel.writeAndFlush(Unpooled.wrappedBuffer((byte[]) channelBuildPacket.getMsg()));
                userChannel.pipeline().fireUserEventTriggered(new TransferEvent());
            }
            userChannel.config().setAutoRead(true);
        }
    }
}
