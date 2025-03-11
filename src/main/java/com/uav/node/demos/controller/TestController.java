package com.example.uav.controller;

import com.example.uav.config.FiscoBcos;
import com.example.uav.contract.HelloWorld;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    FiscoBcos fiscoBcos;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() throws ContractException {
        BcosSDK sdk =  fiscoBcos.getBcosSDK();
        // 为群组group初始化client
        Client client = sdk.getClient("group0");
        // 获取群组1的块高
        BlockNumber blockNumber = client.getBlockNumber();
        // 部署HelloWorld合约
        CryptoKeyPair cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        HelloWorld helloWorld = HelloWorld.deploy(client, cryptoKeyPair);
        // 调用HelloWorld合约的get接口
        String getValue = helloWorld.get();
        // 调用HelloWorld合约的set接口
        TransactionReceipt receipt = helloWorld.set("Hello, fisco");
        System.out.println(receipt);
        return ResponseEntity.ok("hello world!");
    }
}
