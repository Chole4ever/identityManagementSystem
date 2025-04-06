package com.uav.node.demos.service;

import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.DDO;
import com.uav.node.demos.model.GDDO;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.uav.node.demos.model.GDDO.splitDIDList;


@Service
public class SmartContractService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;
    private AssembleTransactionProcessor transactionProcessor;

    @Value("${uav.didRegistryContractAddress}")
    private String didRegistryContractAddress;

    @Value("${uav.gdidRegistryContractAddress}")
    private String gdidRegistryContractAddress;
    Logger logger = LoggerFactory.getLogger(SmartContractService.class);

    public SmartContractService(FiscoBcos fiscoBcos) throws IOException {
        BcosSDK sdk = fiscoBcos.getBcosSDK();
        client = sdk.getClient("group0");
        cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
    }

    public DDO findDID(String did) throws IOException, TransactionBaseException, ContractCodecException {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/DIDRegistry.abi",
                                "src/main/resources/bin/DIDRegistry.bin");

        List<Object> params = new ArrayList<>();
        params.add(did);

        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "DIDRegistry",
                        didRegistryContractAddress,
                        "getDIDDocument",
                        params);
        List<Object> list =  transactionResponse.getReturnObject();
        DDO ddo = new DDO();
        String did_ = (String) list.get(0);
        String gdid = (String) list.get(1);
        List<String> PublicKeys = (List<String>) list.get(2);
        List<String> ServerLists = (List<String>) list.get(3);
        ddo.setDid(did_);
        ddo.setGdid(gdid);
        ddo.setPublicKeys(new String[]{PublicKeys.get(0)});
        ddo.setServiceList(new String[]{ServerLists.get(0)});
        logger.info("findDID :{} ,DDO: {}",did,ddo);
        return ddo;
    }

    public GDDO findGDID(String did) throws IOException, TransactionBaseException, ContractCodecException {
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/GDIDRegistry.abi",
                                "src/main/resources/bin/GDIDRegistry.bin");

        List<Object> params = new ArrayList<>();
        params.add(did);

        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "DIDRegistry",
                        didRegistryContractAddress,
                        "getDIDDocument",
                        params);
        List<Object> list =  transactionResponse.getReturnObject();
        GDDO gddo = new GDDO();

        String gdid = (String) list.get(0);
        String PublicKeys = (String) list.get(1);
        String ServerLists = (String) list.get(2);
        String DIDListsRaw = (String) list.get(3);
        String[] DIDLists = splitDIDList(DIDListsRaw);

        int seq = (int) list.get(4);

        gddo.setGdid(gdid);
        gddo.setPublicKeys(new String[]{PublicKeys});
        gddo.setServiceList(new String[]{ServerLists});
        gddo.setDidList(DIDLists);
        gddo.setSeq(seq);
        return gddo;
    }




    public void registerGDID(String gdid, byte[] pkList, List<String> serverList, List<String> didLists, byte[] aggr) throws TransactionBaseException, ContractCodecException, IOException {

        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/GDIDRegistry.abi",
                                "src/main/resources/bin/GDIDRegistry.bin");

        List<Object> params = wrapParams(gdid,pkList,serverList,didLists);
        logger.info("preparing registerGDID params {} for smart contract...",params);
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "GDIDRegistry",
                        gdidRegistryContractAddress,
                        "registerGDID",
                        params);
        logger.info(transactionResponse.getReceiptMessages());
        logger.info(transactionResponse.getEvents());
        logger.info("Group DID generation finished.");
        logger.info(transactionResponse.toString());
    }

    private static List<Object> wrapParams(String gdid_,byte[] pkList,List<String> servers,List<String> dids)
    {

        DynamicBytes[] pkBytesArray = new DynamicBytes[]{ new DynamicBytes(pkList) };
        DynamicArray<DynamicBytes> pkListParam = new DynamicArray<>(DynamicBytes.class, pkBytesArray);

        // 构造serverList（string[]）
        List<Utf8String> serverArray = new ArrayList<>();
        for(String a:servers)
        {
            serverArray.add(new Utf8String(a));
        }
        DynamicArray<Utf8String> serverParam = new DynamicArray<>(Utf8String.class, serverArray.toArray(new Utf8String[0]));

        // 构造didLists（string[]）
        List<Utf8String> didArray = new ArrayList<>();
        for(String a:dids)
        {
            didArray.add(new Utf8String(a));
        }
        DynamicArray<Utf8String> didParam = new DynamicArray<>(Utf8String.class, didArray.toArray(new Utf8String[0]));

        // 构造参数列表
        return Arrays.asList(
                new Utf8String(gdid_),
                pkListParam,    // bytes[]
                serverParam,    // string[]
                didParam        // string[]
        );
    }

}

