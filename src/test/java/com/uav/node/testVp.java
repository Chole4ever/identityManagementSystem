package com.uav.node;


import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.Secp256k;
import com.uav.node.demos.model.Claim;
import com.uav.node.demos.model.Credential;
import com.uav.node.demos.service.CredentialService;
import com.uav.node.demos.util.JsonBytesConverter;
import com.uav.node.demos.util.PersistStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

import static com.uav.node.demos.crypto.Secp256k.generateKeyPair;
import static com.uav.node.demos.crypto.Secp256k.signatureDataToBytes;

@SpringBootTest
public class testVp {

    @Autowired
    CredentialService credentialService;

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    @Test
    public void test() throws Exception {
        BigInteger privKey = new BigInteger("40779086466177057605767635656162985036307673144995806911493908486715184715114",10);
        ECKeyPair ecKeyPair = generateKeyPair(privKey);

        Credential credential = new Credential();

        credential.setHolder(config.getGdid());
        credential.setIssuer("did:GCS:2586753285709987093");
        Claim claim = new Claim();
        credential.setClaim(claim);
        byte[] beSigned = claim.toJson().getBytes();
        byte[] msgHash = Hash.sha3(beSigned);

        Sign.SignatureData signMessage = Secp256k.signMessage(msgHash,ecKeyPair);
        byte[] proof = signatureDataToBytes(signMessage);
        credential.setProof(proof);

        PersistStore persistStore = new PersistStore();
        persistStore.wirteToFile("GroupCredential","GroupCredential",credential.toJson().getBytes());


        byte[] credential1 = persistStore.loadFromFile("./keystore/GroupCredential.json","GroupCredential");

        Credential credential2 = JsonBytesConverter.fromBytes(credential1,Credential.class);
        System.out.println(credential2);
        credentialService.verifyCredential(credential2);

    }
}
