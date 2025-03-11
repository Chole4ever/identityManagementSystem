package com.uav.node.demos.network;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.service.MessageService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import com.uav.node.demos.model.Message;

@Component
public class UdpServerHandler extends SimpleChannelInboundHandler<Message> {
    Logger logger = LoggerFactory.getLogger(UdpServerHandler.class);

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    @Autowired
    MessageService messageService;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info(String.valueOf(cause));
        ctx.close();
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if(message==null) {
            return;
        } else {
            logger.info("node {} read broadcast message {}",config.getOwnerId(),message.toGood());
            messageService.processMessage(message);
        }
    }
}