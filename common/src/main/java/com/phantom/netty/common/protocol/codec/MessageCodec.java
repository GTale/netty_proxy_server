package com.phantom.netty.common.protocol.codec;


import com.phantom.netty.common.protocol.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;

@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Packet> {

    public static final MessageCodec INSTANCE = new MessageCodec();

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet,
                          List<Object> list) throws Exception {
        ByteBuf byteBuf = ctx.channel().alloc().ioBuffer();
        ProtocolCodec.encode(byteBuf, packet);
        list.add(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        Packet packet = ProtocolCodec.decode(byteBuf);
        if (packet == null) {
            ctx.channel().close();
        } else {
            list.add(packet);
        }
    }
}
