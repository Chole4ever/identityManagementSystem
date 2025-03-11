package com.example.uav.service;

import com.example.uav.config.FiscoBcos;
import com.example.uav.crypto.DKG;
import com.example.uav.model.Message;
import com.example.uav.network.tcp.NettyClientPool;

import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.Buffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.uav.crypto.BLS.aggregateSignatures;
import static com.example.uav.crypto.DKG.*;
import static com.example.uav.model.Message.warpMessage;

@Service
public class GDIDService {

    @Value("${uav.memberIds}")
    private int id;
    @Value("${group.leaderId}")
    private int leaderId;
    @Value("${DKG.threshold}")
    private int threshold;
    @Value("${DKG.count}")
    private int count;
   // @Autowired
   // TransportService transportService;
//
//    @Value("${UAV.didRegistryContractAddress}")
//    private String didRegistryContractAddress;
//
//    Logger logger = LoggerFactory.getLogger(GDIDService.class);
//
//    @Autowired
//    FiscoBcos fiscoBcos;
//
//
//    private  BigInteger groupPK;
//
//    List<BigInteger> subShares = new ArrayList<>();
//
//    List<BigInteger> subPKShares = new ArrayList<>();
//
//    List<BigInteger> subSigs= new ArrayList<>();
//
//
//    private static final DKG dkg = new DKG();
//
//    public void LaunchGDIDGeneration() throws Exception {
//        Message message = warpMessage(BigInteger.valueOf(0),id,"Launch_GDID_Generation");
//      //  transportService.SendBroadcastMessage(message);
//    }
//
//    private void GDIDRegister(List<BigInteger> subSigs) throws IOException, NoSuchAlgorithmException, TransactionBaseException, ContractCodecException {
//
//        //1.agg
//        BigInteger[] bigIntegers = new BigInteger[subSigs.size()];
//        for(int i=0;i<subSigs.size();i++)bigIntegers[i] = subSigs.get(i);
//        BigInteger aggsig = aggregateSignatures(bigIntegers);
//
//        BcosSDK sdk =  fiscoBcos.getBcosSDK();
//        Client client = sdk.getClient("group0");
//
//        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
//        AssembleTransactionProcessor transactionProcessor =
//                TransactionProcessorFactory.createAssembleTransactionProcessor
//                        (client, cryptoKeyPair,
//                                "src/main/resources/abi/DIDRegistry.abi",
//                                "src/main/resources/bin/DIDRegistry.bin");
//
//        //2.gdid
//        String gdid = generateDID("hello");
//        List<Object> params = new ArrayList<>();
//        params.add(gdid);
//        params.add(groupPK);
//        params.add("test");
//
//        TransactionResponse transactionResponse =
//                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
//                        "DIDRegistry",
//                        didRegistryContractAddress,
//                        "registerDID",
//                        params);
//
//        logger.info("Group DID generation finished\n");
//        logger.info(transactionResponse.toString());
//    }
//
//    private  void SendSubGPKShared() {
//        BigInteger pkshares = dkg.CaculateSubGPKShared();
//        //向领导节点发送
//        Message message = warpMessage(pkshares,id,"SEND_PUB_KEY_SHARED");
//        transportService.SendTcpMessage(message,leaderId);
//
//    }
//    private void SendSubSKShared() {
//        List<BigInteger> skshares = dkg.CaculateSubSKShared();
//        //向其他 n-1 节点发送
//        for(int i=0;i<count;i++)
//        {
//            //向其他 n-1 节点发送
//            if(i!=id)
//            {
//                Message message = warpMessage(skshares.get(i),id,"SEND_SUB_SHARED");
//                transportService.SendTcpMessage(message,i);
//            }
//        }
//    }
//    private void sendGDIDGenerationRq(BigInteger groupPK) throws Exception {
//        Message message = warpMessage(groupPK,id,"Launch_GDID_Generation");
//     //   transportService.SendBroadcastMessage(message);
//    }
//    private void SendSubSig(BigInteger sig) {
//        Message message = warpMessage(sig,id,"GDID_GENERATION_RESPONSE");
//        transportService.SendTcpMessage(message,leaderId);
//    }
//
//
//    public void processMessage(Message message) throws Exception {
//        switch (message.getCommand()) {
//            case "Launch_GDID_Generation":
//                dkg.initDKG(threshold,count);
//                SendSubGPKShared();
//                SendSubSKShared();
//            case "SEND_SUB_SHARED":
//                subShares.add(message.getBigInteger());
//                if(subShares.size()==count) {
//                   dkg.CaculateSubKey(subShares);
//                }
//                break;
//            case "SEND_PUB_KEY_SHARED":
//                subPKShares.add(message.getBigInteger());
//                if(subPKShares.size()==count)
//                {
//                    groupPK = dkg.CaculateGPK(subPKShares);
//                    sendGDIDGenerationRq(groupPK);
//                }
//                break;
//            case "BROADG_DID_GENERATION_REQUEST":
//                BigInteger sig = dkg.CaculateSubSignature(message);
//                SendSubSig(sig);
//                break;
//            case "GDID_GENERATION_RESPONSE":
//                subSigs.add(message.getBigInteger());
//                if(subSigs.size()>threshold)
//                {
//                    GDIDRegister(subSigs);
//                }
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown message type: " + message.getCommand());
//        }
//    }
//
//    public String generateDID(String name) throws NoSuchAlgorithmException {
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] hashBytes = digest.digest(name.getBytes());
//
//        long hashValue = 0;
//        for (int i = 0; i < 8; i++) {
//            hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
//        }
//        if (hashValue < 0) {
//            hashValue = -hashValue;
//        }
//        return "did:UAVGroup:"+hashValue;
//    }


}
