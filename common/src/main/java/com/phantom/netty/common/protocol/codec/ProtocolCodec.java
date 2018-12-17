package com.phantom.netty.common.protocol.codec;

import com.phantom.netty.common.protocol.packet.Packet;
import com.phantom.netty.common.protocol.packet.PacketType;
import com.phantom.netty.common.util.serialize.Serializer;
import com.phantom.netty.common.util.serialize.SerializerAlgorithm;
import io.netty.buffer.ByteBuf;

/**
 * 协议编码,用于加密和解密
 */
public class ProtocolCodec {

    // 定义魔术数
    public static int MAGIC_NUMBER = 0x923423fe;

    /**
     * 加密
     */
    public static void encode(ByteBuf byteBuf, Packet packet) {
        byte[] bytes = Serializer.DEFAULT.serialize(packet);
        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(packet.getVersion());
        byteBuf.writeByte(Serializer.DEFAULT.getSerializerAlgorithm());
        byteBuf.writeByte(packet.getPacketType());
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

    /**
     * 解码
     */
    public static Packet decode(ByteBuf byteBuf) {
        byteBuf.skipBytes(4);
        byteBuf.skipBytes(1);
        //序列化类型
        Byte algorithm = byteBuf.readByte();
        //包类型
        Byte type = byteBuf.readByte();
        //字节长度
        int length = byteBuf.readInt();
        byte[] content = new byte[length];
        byteBuf.readBytes(content);
        //反序列化
        SerializerAlgorithm serializer = SerializerAlgorithm.getAlgorithm(algorithm);
        PacketType packetType = PacketType.getPacketType(type);

        if (serializer != null && packetType != null) {
            try {
                return serializer.getClazz().newInstance().deserialize(packetType.getClazz(), content);
            } catch (Exception e) {
                System.err.println("反序列化失败");
            }
        }

        return null;
    }
}
