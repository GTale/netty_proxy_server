package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用处理器
 */
@ChannelHandler.Sharable
public class CommonHandler extends SimpleChannelInboundHandler<Packet> {

    public static CommonHandler INSTANCE = new CommonHandler();
    private Map<Byte, SimpleChannelInboundHandler<? extends Packet>> handlerMap;

    private CommonHandler() {
        handlerMap = new HashMap<>();
        handlerMap.put(PacketType.HEARTBEAT_REQUEST.getValue(), HeartBeatRequestHandler.INSTANCE);
        handlerMap.put(PacketType.DYNAMIC_PORT.getValue(), DynamicRequestHandler.INSTANCE);
        handlerMap.put(PacketType.DISPATCHER_RESPONSE.getValue(), DispatcherResponseHandler.INSTANCE);
        handlerMap.put(PacketType.CHANNEL_BUILD_RESPONSE.getValue(), ChannelBuildResponseHandler.INSTANCE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet msg) throws Exception {
        handlerMap.get(msg.getPacketType()).channelRead(ctx, msg);
    }
}
