package com.phantom.netty.client.util;

import com.phantom.netty.common.util.session.Attributes;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: phantom
 * @Date: 2018/12/6 16:35
 * @Description:
 */
public class ClientSessionUtil {
    @Setter
    @Getter
    private static volatile Channel channel;
    @Setter
    private static Boolean result;
    @Setter
    private static String exception;
    private static final Map<String, Channel> remoteChannelGroup = new ConcurrentHashMap<>();
    private static final AttributeKey<String> Sequence = AttributeKey.newInstance("sequence");

    /**
     * 是否已经登陆
     */
    public static boolean hasLogin() {
        return channel != null;
    }

    /**
     * 是否登陆完成
     */
    public static boolean isDone() {
        return result != null;
    }

    /**
     * 是否发生异常
     */
    public static boolean occurException() {
        return !StringUtils.isEmpty(exception);
    }

    /**
     * 获取登陆的异常信息
     */
    public static String getException() {
        return exception;
    }

    /**
     * 绑定真实服务器的channel
     */
    public static void bindSocketChannel(String sequenceId, Channel channel) {
        channel.attr(Sequence).set(sequenceId);
        remoteChannelGroup.put(sequenceId, channel);
    }

    /**
     * 获取真实服务器channel
     */
    public static Channel getSocketChannel(String sequenceId) {
        return remoteChannelGroup.get(sequenceId);
    }

    /**
     * 获取序列号
     */
    public static String getSequence(Channel channel) {
        return channel.attr(Sequence).get();
    }

    /**
     * 获取proxyType
     */
    public static byte getProxyType(Channel realServerChannel) {
        if (realServerChannel.hasAttr(Attributes.PROXY_TYPE)) {
            return realServerChannel.attr(Attributes.PROXY_TYPE).get();
        } else {
            return 0;
        }
    }

    /**
     * 重置所有的数据
     */
    public static void reset() {
        channel = null;
        result = null;
        exception = null;
        remoteChannelGroup.clear();
    }

    public static boolean isShouldSSl(Channel realServerChannel) {
        if (realServerChannel.hasAttr(Attributes.ShouldSSL)) {
            return realServerChannel.attr(Attributes.ShouldSSL).get();
        } else {
            return false;
        }
    }
}
