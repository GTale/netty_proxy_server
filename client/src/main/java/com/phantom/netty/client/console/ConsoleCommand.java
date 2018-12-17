package com.phantom.netty.client.console;

import io.netty.channel.Channel;

import java.util.Scanner;

/**
 * @author: phantom
 * @Date: 2018/11/29 14:51
 * @Description:
 */
public interface ConsoleCommand {

    void exec(Scanner scanner, Channel channel);
}
