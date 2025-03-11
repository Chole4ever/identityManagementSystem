package com.uav.node.demos.controller;


import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.contract.HelloWorld;
import com.uav.node.demos.model.Message;
import com.uav.node.demos.network.udp.UDPClient;
import com.uav.node.demos.service.*;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.client.protocol.response.BlockNumber;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.model.TransactionReceipt;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Message m = new Message(0,"hh");
        udpClient.send(m,1);

        return ResponseEntity.ok("hello world!");
    }


    @GetMapping("/generateGDID")
    public ResponseEntity<Map<String, String>> gd() throws Exception {

        String gdid = gdidService.generateDID("test");

        Map<String, String> response = new HashMap<>();
        response.put("GDID",gdid);
        return ResponseEntity.ok(response);

    }

    @Autowired
    UdpNetworkService udpNetworkService;
    @Autowired
    CryptoService cryptoService;

    @Autowired
    MessageService messageService;
    @Autowired
    SmartContractService smartContractService;
    @GetMapping("/g1")
    public ResponseEntity<String> g1() throws Exception {
        udpNetworkService.print();
        cryptoService.print();
        messageService.print();
        cryptoService.print2();
        udpNetworkService.print2();
        cryptoService.print3();
        messageService.print2();
        cryptoService.print4();
        smartContractService.print5();
        return ResponseEntity.ok("hello");


    }
    @GetMapping("/g5")
    public ResponseEntity< Map<String, String>> g5() throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("Behavior","authenticateGroup");
        response.put("from","did:group:10292847314256823");
        response.put("to","did:group:67894378434256823");
        response.put("Status","success");
        response.put("Verifiable group credential","\"response\": {" +
                "    \"id\": \"did:UAVGroupCredential:7163978434256823\"," +
                "    \"type\": [\"VerifiableCredential\", \"UAVGroupAuthorizationCredential\"]," +
                "    \"issuer\": \"did:group:10292847314256823\"," +
                "    \"issuanceDate\": \"2025-02-18T12:00:00Z\"," +
                "    \"expirationDate\": \"2025-10-06T12:00:00Z\"," +
                "    \"credentialSubject\": {\n" +
                "      \"gdid\": \"did:group:67894378434256823\",\n" +
                "      \"claimHash\": \"(aa8d1fae51333a61288hj89ff7f38488aa4c2d88e3e21a306b765e8997a1c7db0da7196767dd8459d5ce721fefc8ad, 022186b364ef3asdjk34432f36589a981e5ec2bb669239a653f1e412f2c0f556ce61348388462772b7d0d293)\"\n" +
                "    },\n" +
                "    \"proof\": {\n" +
                "      \"type\": \"BLSSignature12-381\",\n" +
                "      \"created\": \"2025-02-15T12:00:00Z\",\n" +
                "      \"proofValue\": \"0cc8d1fae51333a511dde3a89ff7f38488aa4c2d88e3e21a306b765e8997a1c7db0da7196767dd8459d5ce721fefc8ad,022186b364ef351efc936d387f7ab22f36589a981e5ec2bb669239a653f1e412f2c0f556ce61348388462772b7d0d293\"\n" +
                "    }\n" +
                "  }");
        return ResponseEntity.ok(response);

    }







}
