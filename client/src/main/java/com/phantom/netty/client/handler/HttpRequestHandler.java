package com.phantom.netty.client.handler;

import com.phantom.netty.client.util.ClientSessionUtil;
import com.phantom.netty.client.util.GuiState;
import com.phantom.netty.common.protocol.codec.HttpRequestMessageEncoder;
import com.phantom.netty.common.protocol.common.CommonType;
import com.phantom.netty.common.util.PathUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HttpRequestHandler extends ChannelInboundHandlerAdapter {


    private HttpRequestMessageEncoder encoder;

    public HttpRequestHandler(HttpRequestMessageEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * 处理接收到的HttpRequest信息或者心跳包
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            // 发送到真实服务器
            handleHttpMessage(ctx, (FullHttpRequest) msg);
        } else {
            // 丢弃心跳包
        }
    }

    /**
     * 处理Http请求
     */
    private void handleHttpMessage(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 获取存储的sequenceId
        String sequenceId = request.headers().get(CommonType.sequence);
        if (sequenceId == null) {
            GuiState.log("消息异常,获取sequence_id失败");
        } else {
            PathUtil.FullPath fullPath = PathUtil.resolveHttpProxyPath(request.uri());
            Channel realServerChannel = ClientSessionUtil.getSocketChannel(sequenceId);
            if (realServerChannel != null && realServerChannel.isActive()) {
                // 修改数据包
                request.headers().remove(CommonType.sequence);
                request.setUri(fullPath.getPath());

                // 编码发送
                List<Object> output = new ArrayList<>();
                encoder.encode(ctx, request, output);
                output.forEach(realServerChannel::writeAndFlush);
            } else {
                GuiState.log("服务器已断开,消息序号为:" + sequenceId);
            }
        }
    }
}
