package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import lombok.Data;


/**
 * @author: phantom
 * @Date: 2018/11/28 11:52
 * @Description:
 */
@Data
public class LoginResponsePacket extends Packet {
    private boolean success;
    private String userId;
    private String username;
    private String reason;

    @Override
    public Byte getPacketType() {
        return PacketType.LOGIN_RESPONSE.getValue();
    }
}
