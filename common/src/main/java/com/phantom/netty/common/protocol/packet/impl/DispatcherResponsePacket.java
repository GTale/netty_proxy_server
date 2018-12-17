package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import lombok.Data;

/**
 * @author: phantom
 * @Date: 2018/11/30 08:54
 * @Description: 返回对应的数据流
 */
@Data
public class DispatcherResponsePacket extends Packet {

    private String sequenceId;
    private Object msg;

    @Override
    public Byte getPacketType() {
        return PacketType.DISPATCHER_RESPONSE.getValue();
    }
}
