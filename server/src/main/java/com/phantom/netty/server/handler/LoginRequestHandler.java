package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.packet.impl.LoginRequestPacket;
import com.phantom.netty.common.protocol.packet.impl.LoginResponsePacket;
import com.phantom.netty.common.util.session.Session;
import com.phantom.netty.common.util.session.SessionUtil;
import com.phantom.netty.server.server.MainServer;
import com.phantom.netty.server.util.BeanUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class LoginRequestHandler extends SimpleChannelInboundHandler<LoginRequestPacket> {

    public static LoginRequestHandler INSTANCE = new LoginRequestHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LoginRequestPacket loginRequestPacket) {
        LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
        loginResponsePacket.setVersion(loginRequestPacket.getVersion());

        String token = valid(loginRequestPacket);
        if (token != null) {
            loginResponsePacket.setSuccess(true);
            loginResponsePacket.setUserId(token);
            SessionUtil.bindSession(new Session(token, loginRequestPacket.getUsername()), channelHandlerContext.channel());
        } else {
            loginResponsePacket.setReason("账号密码校验失败");
            loginResponsePacket.setSuccess(false);
        }

        channelHandlerContext.channel().writeAndFlush(loginResponsePacket);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionUtil.unBindSession(ctx.channel());
    }

    private String valid(LoginRequestPacket loginRequestPacket) {
        MainServer server = BeanUtil.getBean(MainServer.class);
        if (server != null) {
            return server.login(loginRequestPacket.getUsername(), loginRequestPacket.getPassword());
        }
        return null;
    }
}
