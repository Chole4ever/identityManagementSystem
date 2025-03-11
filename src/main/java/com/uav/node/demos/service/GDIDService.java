package com.uav.node.demos.service;

import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.DKGService;
import com.uav.node.demos.model.GDDO;
import com.uav.node.demos.model.Message;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




@Service
public class GDIDService {

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Autowired
    TransportService transportService;
    @Autowired
    SmartContractService smartContractService;
    @Autowired
    DKGService dkgService;
    @Autowired
    CryptoBean cryptoBean;
    Logger logger = LoggerFactory.getLogger(GDIDService.class);

    public void launchGDIDGeneration(GDDO gddo) throws Exception {
        logger.info("node "+config.getOwnerId()+" launches GDIDGeneration...");
        Message message = new Message(config.getOwnerId(),"Launch_GDID_Generation");
        transportService.sendBroadcastMessage(message);
    }
    public void launchGdidRR() throws Exception {
        logger.info("node "+config.getOwnerId()+" launches GDID doc registered...");
        String gdid = generateDID("whatever");
        config.setGdid(gdid);

        List<String> didLists = config.getDidLists();

        List<byte[]> pkList = new ArrayList<>();
        byte[] pkbytes = new byte[192];
        cryptoBean.getGroupPubKey().toBytes(pkbytes);
        pkList.add(pkbytes);

        List<String> serverList = new ArrayList<>();
        serverList.add("rescue");serverList.add("transport");serverList.add("monitor");

        byte[] metaData = dataTobytes(gdid,pkList,serverList,didLists);
        logger.info("node "+config.getOwnerId()+" generates hash of gdidrr message ");

        Message message = new Message(config.getOwnerId(),"BROADG_DID_GENERATION_REQUEST",metaData);
        transportService.sendBroadcastMessage(message);

    }

    public void sendRRToSc(ECP agg) throws NoSuchAlgorithmException, TransactionBaseException, ContractCodecException, IOException {

        List<String> didLists = config.getDidLists();
        String leaderdid = didLists.get(config.getLeaderId());


        byte[] pkbytes = new byte[192];
        cryptoBean.getGroupPubKey().toBytes(pkbytes);


        byte[] aggbytes = new byte[96];
        agg.toBytes(aggbytes,false);
        List<String> serverList = new ArrayList<>();
        serverList.add("rescue");serverList.add("transport");serverList.add("monitor");
        smartContractService.registerGDID(config.getGdid(),leaderdid,pkbytes,serverList,didLists,aggbytes);
    }


    private byte[] dataTobytes(String gdid, List<byte[]> pkList, List<String> serverList, List<String> didLists) {
        // 将每个参数转换为字节数组
        byte[] gdidBytes = gdid.getBytes(StandardCharsets.UTF_8);


        // 将 BigInteger 列表转换为字节数组
        ByteArrayOutputStream pkByteStream = new ByteArrayOutputStream();
        for (byte[] pk : pkList) {
            pkByteStream.write(pk,0, pk.length);
        }
        byte[] pkListBytes = pkByteStream.toByteArray();

        // 将 serverList 转换为字节数组
        ByteArrayOutputStream serverByteStream = new ByteArrayOutputStream();
        for (String server : serverList) {
            serverByteStream.write(server.getBytes(StandardCharsets.UTF_8), 0, server.getBytes(StandardCharsets.UTF_8).length);
        }
        byte[] serverListBytes = serverByteStream.toByteArray();

        // 将 didLists 转换为字节数组
        ByteArrayOutputStream didByteStream = new ByteArrayOutputStream();
        for (String did : didLists) {
            didByteStream.write(did.getBytes(StandardCharsets.UTF_8), 0, did.getBytes(StandardCharsets.UTF_8).length);
        }
        byte[] didListsBytes = didByteStream.toByteArray();

        // 拼接所有字节数组
        ByteArrayOutputStream finalByteStream = new ByteArrayOutputStream();
        try {
            finalByteStream.write(gdidBytes);
            finalByteStream.write(pkListBytes);
            finalByteStream.write(serverListBytes);
            finalByteStream.write(didListsBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return finalByteStream.toByteArray();
    }


    public void sendSubGPKShare() throws Exception {
        ECP2[] publicCoeffs = cryptoBean.getPublicCoeffs();
        ECP2 pkshare = publicCoeffs[0];
        byte[] value = new byte[192];
        pkshare.toBytes(value);
        Message message = new Message(config.getOwnerId(),"SEND_PUB_KEY_SHARED",value);
        transportService.sendUDPMessage(message,config.getLeaderId());
    }
    public void sendSubSKShares() throws Exception {
        List<Integer> peerId = config.getPeerIds();
        for(int i=0;i<peerId.size();i++)
        {
            BIG share = dkgService.getSharesById(peerId.get(i));
            Message message = new Message(config.getOwnerId(),"SEND_SUB_KEY_SHARED",bigToBytes(share));
            transportService.sendUDPMessage(message,config.getLeaderId());
        }
    }

    public  String generateDID(String name) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(name.getBytes());

        long hashValue = 0;
        for (int i = 0; i < 8; i++) {
            hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
        }
        if (hashValue < 0) {
            hashValue = -hashValue;
        }
        return "did:group:"+hashValue;
    }
    public static byte[] bigToBytes(BIG num) {
        byte[] byteArray = new byte[48]; // 根据需求调整长度
        num.toBytes(byteArray); // 使用BIG的toBytes方法填充数组
        return byteArray;
    }


}
