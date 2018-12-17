package com.phantom.netty.client.handler;

import com.phantom.netty.client.util.ClientSessionUtil;
import com.phantom.netty.common.protocol.packet.impl.LoginResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponsePacket> {

    public static LoginResponseHandler INSTANCE = new LoginResponseHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginResponsePacket loginResponsePacket) {
        if (loginResponsePacket.isSuccess()) {
            ClientSessionUtil.setException(null);
            ClientSessionUtil.setResult(true);
            ClientSessionUtil.setChannel(channelHandlerContext.channel());
        } else {
            ClientSessionUtil.setException("登陆失败:" + loginResponsePacket.getReason());
            ClientSessionUtil.setResult(false);
        }
    }
}
