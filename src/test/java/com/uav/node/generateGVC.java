package com.uav.node;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uav.node.demos.model.Claim;
import com.uav.node.demos.model.Credential;
import com.uav.node.demos.service.CredentialService;
import com.uav.node.demos.util.PersistStore;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SignatureException;

import static com.uav.node.demos.crypto.Secp256k.*;

@SpringBootTest
public class generateGVC {

    @Autowired
    CredentialService credentialService;

    @Test
    public void generateGVCMethod() throws IOException, TransactionBaseException, ContractCodecException, SignatureException {
        //gcs
        String priS = "40779086466177057605767635656162985036307673144995806911493908486715184715114";
        BigInteger pri = new BigInteger(priS);

        ECKeyPair ecKeyPair = generateKeyPair(pri);
        Credential gvc = new Credential();
        gvc.setIssuer("did:GCS:2586753285709987093");
        gvc.setHolder("did:group:5543662756399088765");
        Claim claim = new Claim();

        byte[] msgHash = Hash.sha3(claim.toJson().getBytes());
        Sign.SignatureData signatureData = signMessage(msgHash,ecKeyPair);
        byte[] proof = signatureDataToBytes(signatureData);

        gvc.setProof(proof);
        gvc.setClaim(claim);
        if(credentialService.verifyCredential(gvc))
        {
            System.out.println("success");
            PersistStore ps = new PersistStore();
            ps.wirteToFile("GroupCredential","GroupCredential",gvc.toJson().getBytes());

        }
    }


}
