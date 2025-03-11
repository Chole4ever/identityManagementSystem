package com.uav.node.demos.service;

import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.config.GlobalConfig;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
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
        transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/DIDRegistry.abi",
                                "src/main/resources/bin/DIDRegistry.bin");
    }


    public void registerGDID(String gdid, String leaderdid, byte[] pkList, List<String> serverList, List<String> didLists, byte[] aggr) throws TransactionBaseException, ContractCodecException, IOException {
        logger.info("preparing registerGDID params for smart contract...");
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/DIDRegistry.abi",
                                "src/main/resources/bin/DIDRegistry.bin");

        List<Object> params = new ArrayList<>();
        params.add(gdid);
        params.add(leaderdid);
        params.add(pkList);
        params.add(serverList);
        params.add(didLists);
        params.add(aggr);

        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "GDIDRegistry",
                        gdidRegistryContractAddress,
                        "registerGDID",
                        params);

        logger.info("Group DID generation finished\n");
        logger.info(transactionResponse.toString());
    }
}

