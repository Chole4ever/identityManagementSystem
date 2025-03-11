package com.uav.node.demos.network.tcp;


import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;
import com.uav.node.demos.service.GDIDService;
import com.uav.node.demos.service.MessageService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class NettyClientHandler extends SimpleChannelInboundHandler<Message> {
    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Autowired
    MessageService messageService;
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if(message==null) {
            return;
        } else {
            logger.info("node "+config.getOwnerId()+" received message "+message.getCommand()+" from "+message.getFromId());
            messageService.processMessage(message);
        }
    }


}
