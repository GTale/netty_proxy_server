package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import lombok.Data;

import java.util.List;

/**
 * @author: phantom
 * @Date: 2018/11/30 11:21
 * @Description: 动态端口数据包
 */
@Data
public class DynamicPortPacket extends Packet {

    /**
     * 端口号
     */
    List<String> ports;

    @Override
    public Byte getPacketType() {
        return PacketType.DYNAMIC_PORT.getValue();
    }
}
