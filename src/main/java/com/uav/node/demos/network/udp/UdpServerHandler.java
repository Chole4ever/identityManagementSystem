//package com.example.uav.network.udp;
//
//
//import com.example.uav.model.Message;
//import com.example.uav.service.GDIDService;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class UdpServerHandler extends SimpleChannelInboundHandler<Message> {
//    Logger logger = LoggerFactory.getLogger(UdpServerHandler.class);
//
//    @Autowired
//    GDIDService gdidService;
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        logger.info(String.valueOf(cause));
//        ctx.close();
//    }
//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
//        if(message==null) {
//            return;
//        } else {
//        //    gdidService.processMessage(message);
//        }
//    }
//}