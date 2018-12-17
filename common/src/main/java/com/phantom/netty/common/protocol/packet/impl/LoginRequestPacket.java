package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import lombok.Data;


/**
 * @author: phantom
 * @Date: 2018/11/28 11:22
 * @Description:
 */
@Data
public class LoginRequestPacket extends Packet{

    private String userId;
    private String username;
    private String password;


    @Override
    public Byte getPacketType() {
        return PacketType.LOGIN_REQUEST.getValue();
    }
}
