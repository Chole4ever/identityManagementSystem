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

    @Autowired
    UDPClient udpClient;

    //DIDG验证
    @GetMapping("/test")
    public ResponseEntity<String> test() throws Exception {
        gdidService.launchGDIDGeneration();
        return ResponseEntity.ok("hello world!");
    }

    //本次SK存储
    @GetMapping("/storeSK")
    public ResponseEntity<String> test4() throws Exception {
        gdidService.storeSK();
        return ResponseEntity.ok("hello world!");
    }





}
