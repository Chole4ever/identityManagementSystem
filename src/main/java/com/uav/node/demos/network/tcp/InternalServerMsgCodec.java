package com.uav.node.demos.network.tcp;


import com.uav.node.demos.model.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class InternalServerMsgCodec  extends
        CombinedChannelDuplexHandler<InternalServerMsgCodec.Decoder, InternalServerMsgCodec.Encoder> {
    public InternalServerMsgCodec(Class<?> decoderClz, Class<?> encoderClz) {
        super.init(new Decoder(decoderClz), new Encoder(encoderClz));
    }

    static final class Decoder extends ByteToMessageDecoder {
        private Class<?> genericClass; // 待编码的对象类型

        public Decoder(Class<?> genericClass) {
            this.genericClass = genericClass;
        }

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
            // Ensure the buffer has enough data for the length field (4 bytes for an integer)
            if (in.readableBytes() < Integer.SIZE / 8) {
                return;
            }

            in.markReaderIndex();

            int fromId = in.readInt();

            if (in.readableBytes() < Integer.SIZE / 8) {
                return;
            }

            int commandLen = in.readableBytes();
            String command = "unknown";
            if (commandLen > 0) {
                ByteBuf commandBuf = in.readBytes(commandLen);
                command = commandBuf.toString(StandardCharsets.UTF_8);  // Convert ByteBuf to String
            }

            if (in.readableBytes() < Integer.SIZE / 8) {
                return;
            }

            int value =  in.readableBytes();
            if(value>0)
            {
                ByteBuf addMsg = in.readBytes(value);
                list.add(new Message(fromId,command, addMsg.array()));
            }else list.add(new Message(fromId,command));


        }
    }

    static final class Encoder extends MessageToByteEncoder<Message> {
        private Class<?> genericClass; // 待编码的对象类型

        public Encoder(Class<?> genericClass) {
            this.genericClass = genericClass;
        }

        @Override
        protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
            if (genericClass.isInstance(message)) {

                int fromId = message.getFromId();

                byteBuf.writeInt(fromId);//id


                String command = message.getCommand();
                if (command != null && !command.isEmpty()) {
                    // Convert the command to bytes using UTF-8 encoding
                    byte[] commandBytes = command.getBytes(StandardCharsets.UTF_8);
                    // Write the length of the command byte array
                    byteBuf.writeInt(commandBytes.length);
                    // Write the command bytes into the ByteBuf
                    byteBuf.writeBytes(commandBytes);
                }

                byte[] addMsg = message.getValue();
                if(addMsg!=null&&addMsg.length>0)
                {
                    byteBuf.writeInt(addMsg.length);
                    byteBuf.writeBytes(addMsg);
                }else byteBuf.writeInt(0);

            }
        }
    }
}
