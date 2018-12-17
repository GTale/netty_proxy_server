package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.codec.HttpRequestMessageEncoder;
import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.protocol.packet.impl.DispatcherRequestPacket;
import com.phantom.netty.common.util.TaskPool;
import com.phantom.netty.server.event.TransferEvent;
import com.phantom.netty.server.util.UserTokenUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HttpMessageHandler extends ChannelInboundHandlerAdapter {

    private volatile boolean                   transfer = false;
    private          FullHttpRequest           messages;
    private          HttpRequestMessageEncoder encoder;

    public HttpMessageHandler() {
        encoder = new HttpRequestMessageEncoder();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 处理传递过来的http请求
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            // 判断当前连接是否已经完成,存储,volatile不能保证原子性，需要加锁
            synchronized (this) {
                if (!transfer) {
                    messages = request;
                    return;
                }
            }
            //处理http消息
            messageHandle(ctx, request);
        } else {
            // 直接下发消息
            String                  sequenceId = ctx.channel().id().asShortText();
            DispatcherRequestPacket packet     = new DispatcherRequestPacket();
            packet.setSequenceId(sequenceId);
            packet.setMessageType(CommonType.MessageType.TYPE_MESSAGE);
            packet.setProxyType(CommonType.ProxyType.TUNNEL);
            packet.setUserToken(UserTokenUtil.getUserToken(ctx.channel()));

            ByteBuf buf   = (ByteBuf) msg;
            byte[]  bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();
            packet.setMsg(bytes);

            TaskPool.addTask(packet);
            ((ByteBuf) msg).release();
        }
    }

    /**
     * 下发消息到客户端
     */
    private void messageHandle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 获取序列号
        String sequenceId = ctx.channel().id().asShortText();
        request.headers().set(CommonType.sequence, sequenceId);

        // 转换消息
        List<Object> messages = new ArrayList<>();
        encoder.encode(ctx, request, messages);

        messages.forEach(m -> {
            // 读取消息
            ByteBuf buf  = (ByteBuf) m;
            byte[]  data = new byte[buf.readableBytes()];
            buf.readBytes(data);
            buf.release();

            // 构造数据包
            DispatcherRequestPacket packet = new DispatcherRequestPacket();
            packet.setSequenceId(sequenceId);
            packet.setMessageType(CommonType.MessageType.TYPE_MESSAGE);
            packet.setProxyType(CommonType.ProxyType.HTTP);
            packet.setMsg(data);
            packet.setUserToken(UserTokenUtil.getUserToken(ctx.channel()));

            TaskPool.addTask(packet);
            request.release();
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof TransferEvent) {
            synchronized (this) {
                this.transfer = true;
            }
            // 发送之前滞留的消息
            messageHandle(ctx, messages);
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }
}
