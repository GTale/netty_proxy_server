package com.phantom.netty.server.util.task;

import com.phantom.netty.common.util.TaskPool;
import com.phantom.netty.common.util.session.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务
 */
@Component
@Slf4j
public class Task {

    @Scheduled(cron = "0 0/1 * * * *")
    private void clearPacket() {
        log.info("[{}],开始进行清理工作", LocalDateTime.now().toString());
        List<String> list = SessionUtil.getLogoutUser();
        list.removeIf(p -> {
            TaskPool.clearPacket(p);
            return true;
        });
    }
}
