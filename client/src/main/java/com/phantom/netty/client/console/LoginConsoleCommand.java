package com.phantom.netty.client.console;

import com.phantom.netty.client.util.AlertUtil;
import com.phantom.netty.client.util.ClientSessionUtil;
import com.phantom.netty.common.protocol.packet.impl.LoginRequestPacket;
import io.netty.channel.Channel;
import javafx.application.Platform;

/**
 * @author: phantom
 * @Date: 2018/11/29 14:54
 * @Description:
 */
public class LoginConsoleCommand {

    private static long MAX_DELAY_TIME = 3 * 1000; //10s

    public static boolean login(Channel channel, String username, String password) {
        LoginRequestPacket loginRequestPacket = new LoginRequestPacket();
        loginRequestPacket.setUsername(username);
        loginRequestPacket.setPassword(password);
        channel.writeAndFlush(loginRequestPacket);
        return waitForLoginResponse();
    }

    private static boolean waitForLoginResponse() {
        long    currentTime = System.currentTimeMillis();
        boolean success     = false;
        for (int i = 0; ; i++) {
            if ((i & 0xff) == 64) {
                long time = System.currentTimeMillis();
                if (time - MAX_DELAY_TIME >= currentTime) {
                    break;
                }
            } else if (ClientSessionUtil.isDone()) {
                if (ClientSessionUtil.occurException()) {
                    Platform.runLater(() -> {
                        AlertUtil.showErrorAlert(ClientSessionUtil.getException());
                        ClientSessionUtil.setResult(null);
                    });
                    return false;
                } else {
                    success = true;
                    break;
                }
            }
        }

        if (!success) {
            Platform.runLater(() -> {
                AlertUtil.showErrorAlert("服务器超时,登陆失败");
            });
        }

        return success;
    }
}