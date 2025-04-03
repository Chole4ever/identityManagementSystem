package com.uav.node.demos.network;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;
import com.uav.node.demos.model.MessageDTO;
import com.uav.node.demos.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.net.InetAddress;

@Component
public class MessageCallbackImpl implements MessageCallback {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    Logger logger = LoggerFactory.getLogger(MessageCallbackImpl.class);
    @Autowired
    MessageService messageService;
    @Override
    public void onMessageReceived(byte[] m, InetAddress address,int port) throws Exception {

        Message message =  Message.fromByteArray(m);
        logger.info("node "+config.getOwnerId()+"收到来自："+message.getFromId()+" ，信息："+message.toGood());
        MessageDTO messageDTO = new MessageDTO(message,address,port);
        messageService.processMessage(messageDTO);

    }
}