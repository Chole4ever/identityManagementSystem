package com.example.uav.controller;


import com.example.uav.config.FiscoBcos;
import com.example.uav.contract.DIDRegistry;
import com.example.uav.crypto.Secp256k;
import com.example.uav.model.Node;
import com.example.uav.service.GDIDService;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@RestController
public class ClientController {
    @Value("${UAV.didRegistryContractAddress}")
    private  String didRegistryContractAddress;

    @Autowired
    FiscoBcos fiscoBcos;

    @Autowired
    GDIDService gdidService;

    @GetMapping("/GenerateECCkeyPair")
    public ResponseEntity<Map<String, String>> generateKeyPair() {
        ECKeyPair eccKeyPair = Secp256k.generateKeyPair();
        Map<String, String> response = new HashMap<>();
        response.put("PubKey", eccKeyPair.getPublicKey().toString());
        response.put("SecretKey", eccKeyPair.getPrivateKey().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GenerateDID")
    public ResponseEntity<Map<String, String>> generateDID(
            @RequestParam("username") String username
    ) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(username.getBytes());

        long hashValue = 0;
        for (int i = 0; i < 8; i++) {
            hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
        }
        if (hashValue < 0) {
            hashValue = -hashValue;
        }
        String did = "did:UAV:"+hashValue;
        Map<String, String> response = new HashMap<>();
        response.put("DID",did);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deployDIDRegistry")
    public ResponseEntity<Map<String, String>> deployDIDRegistry() throws ContractException {
        BcosSDK sdk =  fiscoBcos.getBcosSDK();
        // 为群组group初始化client
        Client client = sdk.getClient("group0");

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();

        DIDRegistry didRegistry = DIDRegistry.deploy(client, cryptoKeyPair);

        Map<String, String> response = new HashMap<>();
        response.put("Contract",didRegistry.getContractAddress());
        response.put("blockNumber", String.valueOf(client.getBlockNumber()));
        response.put("Message","deployDIDRegistry");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/registerDID")
    public ResponseEntity<Map<String, String>> registerDID(@RequestBody Node node) throws IOException, TransactionBaseException, ContractCodecException {
        BcosSDK sdk =  fiscoBcos.getBcosSDK();
        Client client = sdk.getClient("group0");

        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        AssembleTransactionProcessor transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/DIDRegistry.abi",
                                "src/main/resources/bin/DIDRegistry.bin");
        // 创建调用交易函数的参数
        //string memory did, string[] memory publicKeys, string[] memory serviceList
        List<Object> params = new ArrayList<>();
        params.add(node.getDid());
        params.add(node.getPublicKeys());
        params.add(node.getServiceList());
        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "DIDRegistry",
                        didRegistryContractAddress,
                        "registerDID",
                        params);
        Map<String, String> response = new HashMap<>();
        response.put("Message","registerDID");
        response.put("transactionResponse",transactionResponse.getEvents());
        response.put("info",transactionResponse.toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registerGDID")
    public ResponseEntity<String> registerGDID(@RequestBody Node node) throws Exception {
        if(node.isLeader())
        {
           // gdidService.LaunchGDIDGeneration();
        }else{
            return ResponseEntity.ok("非领导节点，无法发起GDID生成！");
        }
        return ResponseEntity.ok("GDID注册成功");
    }

    @PostMapping("/groupAuthentication")
    public ResponseEntity<String> groupAuthentication(@RequestBody Node node) {
        // 逻辑处理：群组认证

        return ResponseEntity.ok("群组认证成功");
    }



}
