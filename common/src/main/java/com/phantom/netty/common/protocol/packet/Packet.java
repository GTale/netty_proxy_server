package com.phantom.netty.common.protocol.packet;

import lombok.Data;

/**
 * 基本的包信息
 */
@Data
public abstract class Packet {
    public Byte   version = 1;
    public String userToken;

    public abstract Byte getPacketType();
}
