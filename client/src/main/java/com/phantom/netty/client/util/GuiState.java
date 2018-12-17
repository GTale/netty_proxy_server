package com.phantom.netty.client.util;

import com.phantom.netty.client.entity.ClientConfig;
import javafx.scene.control.Label;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class GuiState {
    @Setter
    @Getter
    private static ClientConfig                config;
    @Getter
    private static Label                       serverStatus;
    private static DateTimeFormatter           formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Getter
    private static LinkedBlockingQueue<String> queue     = new LinkedBlockingQueue<>();

    public static void init(Label label) {
        serverStatus = label;
    }

    public static void log(String logInfo) {
        queue.offer(LocalDateTime.now().format(formatter) + "\t" + logInfo + "\r\n");
    }

    public static String getLog() {
        return queue.poll();
    }
}
