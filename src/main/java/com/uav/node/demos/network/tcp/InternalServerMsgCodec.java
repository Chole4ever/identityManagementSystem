package com.uav.node.demos.network.coder;


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
            // Mark the current read position for potential reset
            in.markReaderIndex();
            // Read the length of the big integer
            int bigIntegerLen = in.readInt();
            // Ensure there are enough bytes for the big integer data
            if (in.readableBytes() < bigIntegerLen) {
                return;
            }
            // Read the big integer data into a ByteBuf
            ByteBuf bigInteger_ = in.readBytes(bigIntegerLen);

            // Convert ByteBuf to a byte array
            byte[] bigIntegerBytes = new byte[bigInteger_.readableBytes()];
            bigInteger_.readBytes(bigIntegerBytes);

            // Convert the byte array to a BigInteger
            BigInteger bigInteger = new BigInteger(bigIntegerBytes);

            int memberId = in.readInt();

            // Ensure the remaining readable bytes are sufficient for the command string
            int commandLen = in.readableBytes();
            String command = "unknown";
            if (commandLen > 0) {
                ByteBuf commandBuf = in.readBytes(commandLen);
                command = commandBuf.toString(StandardCharsets.UTF_8);  // Convert ByteBuf to String
            }
            list.add(new Message(memberId,bigInteger,command));
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
                // Get the BigInteger from the message
                BigInteger bigInteger = message.getBigInteger();

                // Convert the BigInteger to a byte array
                byte[] bigIntegerBytes = bigInteger.toByteArray();

                // Write the length of the BigInteger byte array
                byteBuf.writeInt(bigIntegerBytes.length);

                byteBuf.writeInt(message.getFromId());
                // Write the BigInteger bytes into the ByteBuf
                byteBuf.writeBytes(bigIntegerBytes);

                // Handle the command part (optional)
                String command = message.getCommand();
                if (command != null && !command.isEmpty()) {
                    // Convert the command to bytes using UTF-8 encoding
                    byte[] commandBytes = command.getBytes(StandardCharsets.UTF_8);

                    // Write the length of the command byte array
                    byteBuf.writeInt(commandBytes.length);

                    // Write the command bytes into the ByteBuf
                    byteBuf.writeBytes(commandBytes);
                }
            }
        }
    }
}
