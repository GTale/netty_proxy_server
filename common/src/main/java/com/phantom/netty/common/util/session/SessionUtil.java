package com.phantom.netty.common.util.session;


import io.netty.channel.Channel;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: phantom
 * @Date: 2018/11/29 11:14
 * @Description:
 */
public class SessionUtil {
    @Getter
    private static final List<String>         logoutUser         = new CopyOnWriteArrayList<>();
    private static final Map<String, Channel> userIdChannelMap   = new ConcurrentHashMap<>();
    private static final Map<String, Channel> remoteChannelGroup = new ConcurrentHashMap<>();

    /**
     * 绑定客户端与服务器的登陆信息
     */
    public static void bindSession(Session session, Channel channel) {
        logoutUser.remove(session.getUserId());
        userIdChannelMap.put(session.getUserId(), channel);
        channel.attr(Attributes.SESSION).set(session);
    }

    public static void unBindSession(Channel channel) {
        if (hasLogin(channel)) {
            userIdChannelMap.remove(getSession(channel).getUserId());
            channel.attr(Attributes.SESSION).set(null);
        }
    }

    public static boolean hasLogin(Channel channel) {
        return channel.hasAttr(Attributes.SESSION);
    }

    public static Session getSession(Channel channel) {
        return channel.attr(Attributes.SESSION).get();
    }

    /**
     * 绑定用户与服务器的channel
     */
    public static void bindSocketChannel(String sequenceId, Channel channel) {
        remoteChannelGroup.put(sequenceId, channel);
    }

    public static Channel getSocketChannel(String sequenceId) {
        return remoteChannelGroup.get(sequenceId);
    }


    /**
     * 离线名单
     */
    public static void addLogoutUser(String userToken) {
        logoutUser.add(userToken);
    }
}
