package com.phantom.netty.common.util;

import io.netty.channel.Channel;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: phantom
 * @Date: 2018/11/30 15:36
 * @Description: 端口绑定
 */
public class PortUtil {
    @Getter
    private static Map<String, Channel> portChannelMap = new ConcurrentHashMap<>();

    public static void bind(String port, Channel channel) {
        portChannelMap.put(port, channel);
    }

    public static Channel getChannel(String port) {
        return portChannelMap.get(port);
    }
}
