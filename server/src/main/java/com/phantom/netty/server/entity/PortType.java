package com.phantom.netty.server.entity;

import lombok.Getter;

/**
 * @author: phantom
 * @Date: 2018/12/6 10:27
 * @Description: 端口的类型
 */
public enum PortType {
    /**
     * tcp类型的转发
     */
    TCP((byte) 0),
    /**
     * http类型代理
     */
    HTTP((byte) 1);

    @Getter
    private byte type;

    PortType(byte type) {
        this.type = type;
    }
}
