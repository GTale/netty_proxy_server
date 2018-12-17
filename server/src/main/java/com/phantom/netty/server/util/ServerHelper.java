package com.phantom.netty.server.util;

import com.phantom.netty.server.entity.ProxyServer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: phantom
 * @Date: 2018/12/6 15:05
 * @Description:
 */
public class ServerHelper {
    @Getter
    private static Map<String, ProxyServer>  serverMap = new ConcurrentHashMap<>();
    @Getter
    private static Map<String, List<Object>> groups    = new ConcurrentHashMap<>();
    private static List<Integer>             ports     = new ArrayList<>();

    /**
     * 添加要启动的端口信息
     */
    public static void addProxyServer(ProxyServer server) {
        ports.add(server.getPort());
        serverMap.put(server.getServerToken(), server);
    }

    /**
     * 获取要启动的端口信息
     */
    public static ProxyServer getProxyServer(String token) {
        return serverMap.get(token);
    }

    /**
     * 绑定NioEventLoopGroup
     */
    public static void bindGroup(String serverToken, List<Object> group) {
        groups.put(serverToken, group);
    }

    /**
     * 获取端口对应的NioEventLoopGroup
     */
    public static List<Object> getBindingGroups(String serverToken) {
        return groups.get(serverToken);
    }

    /**
     * 是否存在相同的端口
     */
    public static boolean containPort(String portToken) {
        return serverMap.containsKey(portToken);
    }

    /**
     * 删除相关的信息
     */
    public static void remove(ProxyServer server) {
        serverMap.remove(server.getServerToken());
        ports.remove(server.getPort());
    }

    /**
     * 移出群组
     */
    public static List<Object> removeGroup(String serverToken) {
        return groups.remove(serverToken);
    }
}
