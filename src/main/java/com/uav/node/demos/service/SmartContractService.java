package com.uav.node.demos.service;

import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.config.GlobalConfig;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

