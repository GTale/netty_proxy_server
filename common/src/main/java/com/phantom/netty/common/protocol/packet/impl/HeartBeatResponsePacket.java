package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;

/**
 * @author: phantom
 * @Date: 2018/11/30 14:34
 * @Description:
 */
public class HeartBeatResponsePacket extends Packet {
    @Override
    public Byte getPacketType() {
        return PacketType.HEARTBEAT_RESPONSE.getValue();
    }
}
