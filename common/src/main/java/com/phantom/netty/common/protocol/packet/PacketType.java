package com.phantom.netty.common.protocol.packet;

import com.phantom.netty.common.protocol.packet.impl.*;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum PacketType {
    LOGIN_REQUEST((byte) 1, LoginRequestPacket.class),
    LOGIN_RESPONSE((byte) 2, LoginResponsePacket.class),
    HEARTBEAT_REQUEST((byte) 3, HeartBeatRequestPacket.class),
    HEARTBEAT_RESPONSE((byte) 4, HeartBeatRequestPacket.class),
    DYNAMIC_PORT((byte) 5, DynamicPortPacket.class),
    DISPATCHER_REQUEST((byte) 6, DispatcherRequestPacket.class),
    DISPATCHER_RESPONSE((byte) 7, DispatcherResponsePacket.class),
    CHANNEL_BUILD_REQUEST((byte) 8, ChannelBuildRequestPacket.class),
    CHANNEL_BUILD_RESPONSE((byte) 9, ChannelBuildResponsePacket.class);

    private Byte                    value;
    private Class<? extends Packet> clazz;

    PacketType(Byte value, Class<? extends Packet> clazz) {
        this.value = value;
        this.clazz = clazz;
    }

    public static PacketType getPacketType(Byte value) {
        return Arrays.stream(PacketType.values()).filter(x -> Objects.equals(x.getValue(), value)).findFirst().orElse(null);
    }
}

