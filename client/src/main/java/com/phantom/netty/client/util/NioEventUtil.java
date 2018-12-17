package com.phantom.netty.client.util;


import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.List;

public class NioEventUtil {
    private static List<NioEventLoopGroup> groups = new ArrayList<>();

    public static void add(NioEventLoopGroup nioEventLoopGroup) {
        groups.add(nioEventLoopGroup);
    }

    public static void close() {
        groups.forEach(NioEventLoopGroup::shutdownGracefully);
    }
}
