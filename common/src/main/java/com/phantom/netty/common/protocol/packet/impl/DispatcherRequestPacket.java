package com.phantom.netty.common.protocol.packet.impl;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import lombok.Data;
import lombok.ToString;

/**
 * @author: phantom
 * @Date: 2018/11/30 08:54
 * @Description: 转发指令数据包
 */
@Data
@ToString
public class DispatcherRequestPacket extends Packet {

    /**
     * 序列号
     */
    private String sequenceId;

    /**
     * 客户端的token[指定要发送给哪个客户端]
     */
    private String clientToken;

    /**
     * 消息的类型,CONNECT,SEND_MSG
     */
    private byte messageType;

    /**
     * 代理的类型,TCP,HTTP,TUNNELED
     */
    private byte proxyType;

    /**
     * 客户端主机名
     */
    private String host;

    /**
     * 客户端要访问的端口
     */
    private int port;

    /**
     * 客户端需要转发的原始消息
     */
    private Object msg;

    @Override
    public Byte getPacketType() {
        return PacketType.DISPATCHER_REQUEST.getValue();
    }
}
