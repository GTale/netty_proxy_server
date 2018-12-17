package com.phantom.netty.server.handler;

import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.protocol.packet.impl.DispatcherRequestPacket;
import com.phantom.netty.common.util.PathUtil;
import com.phantom.netty.common.util.TaskPool;
import com.phantom.netty.common.util.session.Attributes;
import com.phantom.netty.common.util.session.Session;
import com.phantom.netty.common.util.session.SessionUtil;
import com.phantom.netty.server.util.UserTokenUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 获取http请求的相关信息,进行远程连接
 */
@Slf4j
public class HttpConnectionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断消息的类型
        Channel channel         = ctx.channel();
        boolean isConnectMethod = false;

        // 获取channel
        if (msg instanceof HttpRequest) {
            // 判断当前是否已经进行过连接
            if (hasConnected(channel)) {
                // 建立过连接,则交给下一个adapter处理
                ctx.fireChannelRead(msg);
            } else {
                // 阻止下一个包的读取
                channel.config().setAutoRead(false);

                HttpRequest request = (HttpRequest) msg;

                // 远程连接
                DispatcherRequestPacket packet = new DispatcherRequestPacket();
                packet.setMessageType(CommonType.MessageType.TYPE_CONNECT_REAL_SERVER);
                packet.setSequenceId(channel.id().asShortText());
                packet.setUserToken(UserTokenUtil.getUserToken(ctx.channel()));

                // 判断当前的连接方法
                if (request.method() == HttpMethod.CONNECT) {
                    isConnectMethod = true;
                    packet.setProxyType(CommonType.ProxyType.TUNNEL);
                    packet.setMsg(request.protocolVersion().text());
                    // 端口、域名
                    PathUtil.FullPath fullPath = PathUtil.resolveTunnelAddr(request.uri());
                    packet.setHost(fullPath.getHost());
                    packet.setPort(fullPath.getPort());
                    // 移除解码器,聚合器
                    ctx.pipeline().remove(CommonType.HTTP_AGGREGATOR);
                    ctx.pipeline().remove(CommonType.HTTP_REQUEST_DECODER);
                } else {
                    packet.setProxyType(CommonType.ProxyType.HTTP);
                    PathUtil.FullPath fullPath = PathUtil.resolveHttpProxyPath(request.uri());
                    packet.setHost(fullPath.getHost());
                    packet.setPort(fullPath.getPort());
                }

                // 绑定channel
                SessionUtil.bindSocketChannel(packet.getSequenceId(), channel);
                TaskPool.addTask(packet);

                // 标记已经开始了连接工作
                channel.attr(Attributes.SESSION).set(new Session());

                // 清除引用
                ReferenceCountUtil.release(msg);

                // 交给下一个处理,进行消息聚合
                if (!isConnectMethod) {
                    ctx.fireChannelRead(msg);
                }
            }
        } else {
            // Connect 连接后的消息,转发给后面处理
            if (hasConnected(channel)) {
                ctx.fireChannelRead(msg);
            } else {
                ReferenceCountUtil.release(msg);
                log.error("未知消息类型,抛弃消息");
            }
        }
    }

    private boolean hasConnected(Channel channel) {
        return channel.hasAttr(Attributes.SESSION);
    }
}
