package com.example.uav.network.tcp;

import com.example.uav.model.Message;
import com.example.uav.service.GDIDService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class NettyClientHandler extends SimpleChannelInboundHandler<Message> {
    Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);

    @Autowired
    GDIDService gdidService;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        if(message==null) {
            return;
        } else {
          //  gdidService.processMessage(message);
        }
    }


}
