package com.phantom.netty.server.util;

import com.phantom.netty.common.util.session.Attributes;
import io.netty.channel.Channel;

public class UserTokenUtil {

    public static String getUserToken(Channel channel) {
        if (channel.hasAttr(Attributes.USER_TOKEN)) {
            return channel.attr(Attributes.USER_TOKEN).get();
        }
        return null;
    }
}
