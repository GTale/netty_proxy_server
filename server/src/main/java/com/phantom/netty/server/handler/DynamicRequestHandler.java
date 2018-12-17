package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.packet.impl.DynamicPortPacket;
import com.phantom.netty.common.util.PortUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Optional;

/**
 * @author: phantom
 * @Date: 2018/11/30 11:41
 * @Description:
 */
public class DynamicRequestHandler extends SimpleChannelInboundHandler<DynamicPortPacket> {

    public static DynamicRequestHandler INSTANCE = new DynamicRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                DynamicPortPacket dynamicPortPacket) throws Exception {
        // 多端口绑定同一个channel
        Optional.ofNullable(dynamicPortPacket.getPorts()).ifPresent(dy ->
                dy.forEach(port ->
                        PortUtil.bind(port, channelHandlerContext.channel())
                )
        );
    }
}
