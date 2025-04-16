package com.uav.node.demos.controller;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.Secp256k;
import com.uav.node.demos.model.Claim;
import com.uav.node.demos.model.Credential;
import com.uav.node.demos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;

import java.math.BigInteger;

import static com.uav.node.demos.crypto.Secp256k.signatureDataToBytes;


@RestController
public class BGASController {

    @Autowired
    AuthService authService;

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    //bgas验证
    @GetMapping("/authGroup")
    public String authGroup() throws Exception {
        authService.authGroup();
        return "hello";
    }

    @GetMapping("/storeGroupVC")
    public String storeGroupVC() throws Exception {
        BigInteger bigInteger = new BigInteger("40779086466177057605767635656162985036307673144995806911493908486715184715114");

        return "hello";
    }
    @GetMapping("/GCS_issueVC")
    public String GCS_issueVC() throws Exception {


        return "hello";
    }



}
