package com.phantom.netty.common.util;

import com.phantom.netty.common.protocol.packet.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author: phantom
 * @Date: 2018/11/30 10:17
 * @Description: 任务队列
 */
public class TaskPool {
    private static Map<String, LinkedBlockingDeque<Packet>> queue = new HashMap<>();

    public static void addTask(Packet packet) {
        try {
            String                      userToken = packet.getUserToken();
            LinkedBlockingDeque<Packet> deque     = queue.get(userToken);
            if (deque == null) {
                deque = new LinkedBlockingDeque<>();
            }
            deque.add(packet);
            queue.put(userToken, deque);
        } catch (Exception e) {
            System.err.println("添加任务失败,队列已经满了");
        }
    }

    public static Packet get(String userToken) {
        try {
            LinkedBlockingDeque<Packet> deque = queue.get(userToken);
            if (deque != null) {
                return deque.remove();
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearPacket(String userToken) {
        queue.remove(userToken);
    }
}
