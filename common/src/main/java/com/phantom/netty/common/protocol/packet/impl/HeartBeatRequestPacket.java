package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;

public class HeartBeatRequestPacket extends Packet {
    @Override
    public Byte getPacketType() {
        return PacketType.HEARTBEAT_REQUEST.getValue();
    }
}
