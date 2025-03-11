package com.uav.node.demos.network;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;
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
    public void onMessageReceived(String m, InetAddress address) throws Exception {

        Message message =  Message.fromByteArray(m.getBytes());
        logger.info("node "+config.getOwnerId()+" received message "+message.getCommand()+" from "+message.getFromId());
        messageService.processMessage(message);

    }
}