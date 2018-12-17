package com.phantom.netty.server.entity;

import lombok.Data;

/**
 * @author: phantom
 * @Date: 2018/12/6 11:02
 * @Description:
 */
@Data
public class ProxyServer {
    // 所属用户
    private String        userToken;
    // 连接参数
    private Integer       port;
    private byte          proxyType;
    private String        remoteHost;
    private Integer       remotePort;
    private String        serverToken;
    // 连接状态
    private ConnectStatus connectStatus;
    private String        connectMsg;
}
