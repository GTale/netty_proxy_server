package com.phantom.netty.common.protocol.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.util.List;

/**
 * @author: phantom
 * @Date: 2018/12/7 12:55
 * @Description:
 */
public class HttpResponseMessageEncoder extends HttpResponseEncoder {
    public void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        super.encode(ctx, msg, out);
    }
}
