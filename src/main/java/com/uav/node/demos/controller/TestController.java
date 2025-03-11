package com.uav.node.demos.controller;


import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.contract.HelloWorld;
import com.uav.node.demos.model.GDDO;
import com.uav.node.demos.model.Message;
import com.uav.node.demos.network.UDPClient;
import com.uav.node.demos.service.*;
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

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    @Autowired
    FiscoBcos fiscoBcos;
    @Autowired
    TransportService transportService;
    @Autowired
    GDIDService gdidService;



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

    @GetMapping("/udp")
    public ResponseEntity<String> testUDP() throws Exception {
      //  transportService.sendBroadcastMessage(new Message(0,BigInteger.ZERO,"hell0"));
        return ResponseEntity.ok("hello world!");
    }
    @GetMapping("/tcp")
    public ResponseEntity<String> testTCP() throws Exception {
       // transportService.sendTcpMessage(new Message(0,BigInteger.ZERO,"hell0"),0);
        return ResponseEntity.ok("hello world!");
    }
    @GetMapping("/gg")
    public ResponseEntity<String> testGG() throws Exception {

        return ResponseEntity.ok("hello world!");
    }

    @Autowired
    UDPClient udpClient;
    @GetMapping("/test")
    public ResponseEntity<String> test() throws Exception {
        gdidService.launchGDIDGeneration();
        return ResponseEntity.ok("hello world!");
    }


    @GetMapping("/generateGDID")
    public ResponseEntity<Map<String, String>> gd() throws Exception {

        String gdid = gdidService.generateDID("test");

        Map<String, String> response = new HashMap<>();
        response.put("GDID",gdid);
        return ResponseEntity.ok(response);

    }




}
