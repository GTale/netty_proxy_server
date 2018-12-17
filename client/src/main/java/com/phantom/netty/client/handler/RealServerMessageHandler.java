package com.phantom.netty.client.handler;

import com.phantom.netty.client.util.ClientSessionUtil;
import com.phantom.netty.client.util.GuiState;
import com.phantom.netty.common.protocol.codec.HttpResponseMessageEncoder;
import com.phantom.netty.common.protocol.packet.impl.DispatcherResponsePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: phantom
 * @Date: 2018/12/6 16:24
 * @Description:
 */
@Slf4j
public class RealServerMessageHandler extends ChannelInboundHandlerAdapter {

    private HttpResponseMessageEncoder encoder;

    public RealServerMessageHandler(HttpResponseMessageEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        log.info("[Server==>Proxy],收到数据包");
        if (msg instanceof HttpObject) {
            handlerMsg(ctx, (HttpObject)msg);
        } else {
            log.info("[Server==>Proxy],不支持的http消息:丢弃消息," + msg.getClass().getSimpleName());
            ReferenceCountUtil.release(msg);
        }
    }

    private void handlerMsg(ChannelHandlerContext ctx, HttpObject response) throws Exception {
        Channel channel = ClientSessionUtil.getChannel();

        if (channel != null && channel.isActive()) {
            // 编码数据
            List<Object> output = new ArrayList<>();
            encoder.encode(ctx, response, output);

            output.forEach(o -> {
                // 读取数据
                ByteBuf byteBuf = (ByteBuf) o;
                byte[]  bytes   = new byte[byteBuf.readableBytes()];
                byteBuf.readBytes(bytes);
                ReferenceCountUtil.release(byteBuf);

                // 发送到服务端
                DispatcherResponsePacket responsePacket = new DispatcherResponsePacket();
                responsePacket.setSequenceId(ClientSessionUtil.getSequence(ctx.channel()));
                responsePacket.setMsg(bytes);
                channel.writeAndFlush(responsePacket);
//                log.info("[Proxy==>Client],发送数据包");
            });
        } else {
            GuiState.log("客户端与服务器连接已经断开");
        }

        // 不再传递,释放资源
        ReferenceCountUtil.release(response);
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
