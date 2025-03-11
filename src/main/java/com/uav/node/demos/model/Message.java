package com.example.uav.model;

import lombok.Data;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

@Data
public class Message{
    private BigInteger bigInteger;
    private int bigIntegerLen;
    private int memberId;
    private String command;

    public Message(BigInteger bigInteger,int memberId,int bigIntegerLen,String command)
    {
        this.bigInteger = bigInteger;
        this.bigIntegerLen = bigIntegerLen;
        this.memberId = memberId;
        this.command = command;
    }

    public Message() {

    }

    public static Message warpMessage(BigInteger bigInteger,int memberId,String command)
    {
        byte[] byteArray = bigInteger.toByteArray();
        return new Message(bigInteger,memberId,byteArray.length,command);
    }


    public  int getBytes() {
        int bigIntegerBytes = this.bigInteger != null ? this.bigInteger.toByteArray().length : 0;
        int intFieldsBytes = Integer.BYTES * 2;
        int stringBytes = this.command != null ? this.command.getBytes(StandardCharsets.UTF_8).length : 0;

        return bigIntegerBytes + intFieldsBytes + stringBytes;
    }
}
