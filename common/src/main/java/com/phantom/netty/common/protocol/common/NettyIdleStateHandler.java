package com.phantom.netty.common.protocol.common;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 心跳检测
 */
public class NettyIdleStateHandler extends IdleStateHandler {

    private static final int      READER_IDLE_TIME = 15;
    private              Supplier supplier;

    public NettyIdleStateHandler(Supplier supplier) {
        super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
        this.supplier = supplier;
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        System.err.println("心跳检测失败,关闭连接");
        if (supplier != null) {
            supplier.get();
        }
        ctx.channel().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (supplier != null) {
            supplier.get();
        }
    }
}
