package com.uav.node.demos.model;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import lombok.Data;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Data
public class Message{

    /*
    消息结构：
    fromId,bigIntegerLen,bigInteger,command.len,command
     */
    private int fromId;
    private String command;
    private byte[] value;

    public Message() {}

    public Message(int fromId,String command,byte[] value)
    {
        this.fromId = fromId;
        this.command = command;
        this.value = value;
    }
    public Message(int fromId,String command)
    {
        this.fromId = fromId;
        this.command = command;

    }

    public String toGood()
    {
        return "from: "+fromId+
                   "  command: "+command+
                    " value: "+ Arrays.toString(value);
    }

    public byte[] toByteArray() {

        byte[] commandBytes = command.getBytes(StandardCharsets.UTF_8); // String 转字节数组
        int vLen = 0;
        if(value!=null) vLen = value.length;

        ByteBuffer buffer = ByteBuffer.allocate( 4+4+4+commandBytes.length+vLen);  // 每个 int 需要 4 字节

        buffer.putInt(fromId);
        buffer.putInt(commandBytes.length);  // 4 字节：command 字符串的长度
        buffer.put(commandBytes);  // command 字符串数据
        buffer.putInt(vLen);  // 4 字节：command 字符串的长度
        if(vLen>0) buffer.put(value);  // command 字符串数据

        return buffer.array();
    }

    public static Message fromByteArray(byte[] bytes) {
        //   bigIntegerLen,bigInteger,memberId,command.len,command
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        Message message = new Message();

        int id = buffer.getInt();
        message.setFromId(id);

        int commandLen = buffer.getInt();  // 读取 command 字符串长度
        byte[] commandBytes = new byte[commandLen];
        buffer.get(commandBytes);  // 读取 command 字符串
        message.setCommand(new String(commandBytes, StandardCharsets.UTF_8));

        if(buffer.remaining()>0)
        {
            int valueLen = buffer.getInt();  // 读取 command 字符串长度

            byte[] valueBytes = new byte[valueLen];
            buffer.get(valueBytes);  // 读取 command 字符串
            message.setValue(valueBytes);
        }
        return message;
    }

}
