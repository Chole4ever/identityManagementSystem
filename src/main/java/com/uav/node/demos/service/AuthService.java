package com.uav.node.demos.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.uav.node.demos.config.AuthSession;
import com.uav.node.demos.config.CredentialConfig;
import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.*;
import com.uav.node.demos.util.JsonBytesConverter;
import org.apache.milagro.amcl.BLS381.ECP;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.net.InetAddress;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.List;

@Service
public class AuthService {

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Qualifier("cryptoBean")
    @Autowired
    CryptoBean cryptoBean;
    @Qualifier("authSession")
    @Autowired
    AuthSession authSession;
    @Autowired
    CredentialConfig credentialConfig;
    @Autowired
    TransportService transportService;
    @Autowired
    CredentialService credentialService;
    @Autowired
    SmartContractService smartContractService;
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    public void authGroup() throws Exception {
        logger.info("node {} launch group authentication...",config.getOwnerId());
        Message message = new Message(config.getOwnerId(),"InitiateGroupAuth");
        transportService.sendUDPMessage(message, String.valueOf(config.getAuthIp()),config.getAuthPort());
    }

    public void requestLeaderVP(MessageDTO messageDTO) throws Exception {
        InetAddress inetAddress  = messageDTO.getInetAddress();
        Message message = new Message(config.getOwnerId(),"RequestLeaderVP");
        transportService.sendUDPMessage(message,inetAddress.getHostAddress(),messageDTO.getPort());
    }

    public void sendLeaderVP(MessageDTO messageDTO) throws Exception {
        InetAddress inetAddress  = messageDTO.getInetAddress();
        Message message = new Message(config.getOwnerId(),"SubmitLeaderVP");
        Credential leaderCredential = credentialConfig.getCredentials().get(0);
        Presentation leaderVP = new Presentation();
        ECKeyPair ecKeyPair = cryptoBean.getEcKeyPair();
        String did = config.getDid();
        leaderVP.create(leaderCredential,ecKeyPair,did);

        message.setValue(leaderVP.toJson().getBytes());
        transportService.sendUDPMessage(message,inetAddress.getHostAddress(),messageDTO.getPort());

    }
    public boolean verifyVP(MessageDTO messageDTO) {
        Message message = messageDTO.getMessage();
        byte[] presentationsBytes = message.getValue();
        try{
            Presentation presentation = JsonBytesConverter.fromBytes(presentationsBytes,Presentation.class);
            if(presentation.getType()==0) {
                authSession.setCounterpartyLeaderDid(presentation.getHolder());//保存context
            }
            else if(presentation.getType()==1){
                authSession.setCounterpartyGroupGdid(presentation.getHolder());//保存context
            }

            if(credentialService.verifyPresentation(presentation))
            {
                return credentialService.verifyCredential(presentation.getCredentialSubject());
            }else {
                logger.info("verifyVP fails, vp: {}",presentation.toJson());
            }
        }catch (Exception e)
        {
            logger.info("verifyVP {}",e.getCause().getMessage());
        }
        return false;

    }

    public void sendRequestGroupVP(MessageDTO messageDTO) throws Exception {
        String ip  = messageDTO.getInetAddress().getHostAddress();
        int port = messageDTO.getPort();
        Message message = new Message(config.getOwnerId(),"RequestGroupVP");
        transportService.sendUDPMessage(message,ip,port);
    }

    public void sendPrepareGroupVP() throws Exception {
        Message message = new Message(config.getOwnerId(),"PrepareGroupVP");
        transportService.sendBroadcastMessage(message);

    }

    public Presentation sendGVP(ECP agg,MessageDTO messageDTO) throws Exception {
        Credential credential = credentialConfig.getGroupCredentials().get(0);
        Presentation presentation = new Presentation();
        byte[] aggBytes = new byte[97];
        agg.toBytes(aggBytes,false);
        presentation.setProof(aggBytes);
        presentation.setHolder(credential.getHolder());
        presentation.setType(1);
        presentation.setCredentialSubject(credential);

        String ip  = messageDTO.getInetAddress().getHostAddress();
        int port = messageDTO.getPort();

        Message message = new Message(config.getOwnerId(),"FinalizeGroupVP",presentation.toJson().getBytes());

        transportService.sendUDPMessage(message,ip,port);
        return presentation;

    }

    public void requestAggregateSignatures(MessageDTO messageDTO) throws Exception {
        String holderId = authSession.getCounterpartyGroupGdid();
        String issuerId = config.getGdid();
        int type = 1;
        Claim claim = new Claim();

        fillMembers(claim);
        byte[] proof = new byte[0];
        Credential credential = Credential.wrapCredential(claim,type,30,
                issuerId,holderId,proof);
        authSession.setGvc(credential);
        Message message = new Message(config.getOwnerId(),"AggregateSignatures",credential.toJson().getBytes());
        transportService.sendBroadcastMessage(message);
    }

    private void fillMembers(Claim claim) throws TransactionBaseException, ContractCodecException, IOException {

        String gdid = authSession.getCounterpartyGroupGdid();
        GDDO gddo = smartContractService.findGDID(gdid);
        List<String> didList = gddo.getDidList();

        for(String d:didList)
        {
            Claim.UAVMember follower = new Claim.UAVMember();
            follower.setDid(d);
            follower.setRole("Follower");
            claim.getMembers().add(follower);
        }
        // 填充无人机成员
        Claim.UAVMember leader = new Claim.UAVMember();
        leader.setDid(authSession.getCounterpartyLeaderDid());
        leader.setRole("Leader");
        claim.getMembers().add(leader);

    }

    public Credential sendGVC(ECP agg, MessageDTO messageDTO) throws Exception {
        Credential gvc = authSession.getGvc();
        byte[] aggbytes = new byte[97];
        agg.toBytes(aggbytes,false);
        gvc.setProof(aggbytes);

        String ip  = messageDTO.getInetAddress().getHostAddress();
        int port = messageDTO.getPort();

        Message message = new Message(config.getOwnerId(),"IssueGroupCredential",gvc.toJson().getBytes());

        transportService.sendUDPMessage(message,ip,port);

        return gvc;
    }
}
