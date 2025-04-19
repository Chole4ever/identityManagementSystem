package com.uav.node;


import com.uav.node.demos.config.CredentialConfig;
import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.BLSService;
import com.uav.node.demos.crypto.DKGService;
import com.uav.node.demos.crypto.Secp256k;
import com.uav.node.demos.model.Claim;
import com.uav.node.demos.model.Credential;
import com.uav.node.demos.model.GDDO;
import com.uav.node.demos.model.Presentation;
import com.uav.node.demos.service.CredentialService;
import com.uav.node.demos.service.SmartContractService;
import com.uav.node.demos.util.JsonBytesConverter;
import com.uav.node.demos.util.PersistStore;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
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
import java.util.HashMap;

import static com.uav.node.demos.crypto.BLSService.verifyBLSSignature;
import static com.uav.node.demos.crypto.Secp256k.generateKeyPair;
import static com.uav.node.demos.crypto.Secp256k.signatureDataToBytes;

@SpringBootTest
public class testVp {

    @Autowired
    CredentialService credentialService;

    @Autowired
    DKGService dkgService;

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    @Autowired
    CryptoBean cryptoBean;

    @Autowired
    BLSService blsService;

    @Autowired
    SmartContractService smartContractService;
    @Autowired
    CredentialConfig credentialConfig;


    @Test
    public void test() throws Exception {
//        BigInteger privKey = new BigInteger("40779086466177057605767635656162985036307673144995806911493908486715184715114",10);
//        ECKeyPair ecKeyPair = generateKeyPair(privKey);
//
//        Credential credential = new Credential();
//
//        credential.setHolder(config.getGdid());
//        credential.setIssuer("did:GCS:2586753285709987093");
//        Claim claim = new Claim();
//        credential.setClaim(claim);
//        byte[] beSigned = claim.toJson().getBytes();
//        byte[] msgHash = Hash.sha3(beSigned);
//
//        Sign.SignatureData signMessage = Secp256k.signMessage(msgHash,ecKeyPair);
//        byte[] proof = signatureDataToBytes(signMessage);
//        credential.setProof(proof);
//
          PersistStore persistStore = new PersistStore();
//        persistStore.wirteToFile("GroupCredential","GroupCredential",credential.toJson().getBytes());


        byte[] credential1 = persistStore.loadFromFile("./keystore/leaderCredential.json","leaderCredential");

        Credential credential2 = JsonBytesConverter.fromBytes(credential1,Credential.class);
        System.out.println(credential2);
        credentialService.verifyCredential(credential2);

        Presentation leaderVP = new Presentation();
        ECKeyPair ecKeyPair = cryptoBean.getEcKeyPair();
        String did = config.getDid();
        leaderVP.create(credential2,ecKeyPair,did);

        credentialService.verifyPresentation(leaderVP);
    }

    @Test
    public void test2() throws Exception{
        BIG[] sks = new BIG[5];
        PersistStore ps = new PersistStore();

        for(int i=1;i<=5;i++)
        {
            byte[] temp =  ps.loadFromFile("./keystore/skCollection.json","sk"+i);
            sks[i-1] = BIG.fromBytes(temp);
            System.out.println("sk"+i+" "+sks[i-1] );
        }

        byte[] bytes = credentialConfig.getGroupCredentials().get(0).toJson().getBytes();

        HashMap<Integer, ECP> partialSigs = new HashMap<>();
        for(int i=1;i<=5;i++)
        {
            partialSigs.put(i,dkgService.signSig(bytes,sks[i-1]));
        }

        ECP agg = blsService.aggregatedSignatures(partialSigs);

        GDDO gddo = smartContractService.findGDID(config.getGdid());
        byte[] pkList = gddo.getPublicKeys();
        ECP2 pk = ECP2.fromBytes(pkList);

        if(verifyBLSSignature(pk,agg,bytes))
        {
            System.out.println("true");
        }else{
            System.out.println("false");
        }
    }

}
