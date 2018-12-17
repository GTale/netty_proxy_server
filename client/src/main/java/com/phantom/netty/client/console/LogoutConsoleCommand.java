package com.phantom.netty.client.console;

import com.phantom.netty.common.protocol.packet.impl.LogoutRequestPacket;
import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author: phantom
 * @Date: 2018/11/29 14:54
 * @Description:
 */
public class LogoutConsoleCommand implements ConsoleCommand {
    @Override
    public void exec(Scanner scanner, Channel channel) {
        LogoutRequestPacket packet = new LogoutRequestPacket();
        channel.writeAndFlush(packet);
    }
}