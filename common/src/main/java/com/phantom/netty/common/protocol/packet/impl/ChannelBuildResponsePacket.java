package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: phantom
 * @Date: 2018/12/3 14:33
 * @Description: 通道建立消息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelBuildResponsePacket extends Packet {
    private String sequenceId;
    private byte proxyType;
    private String host;
    private int port;
    private Object msg;

    @Override
    public Byte getPacketType() {
        return PacketType.CHANNEL_BUILD_RESPONSE.getValue();
    }
}
