package com.uav.node.demos.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uav.node.demos.model.Claim;
import com.uav.node.demos.model.Credential;
import com.uav.node.demos.util.JsonBytesConverter;
import com.uav.node.demos.util.PersistStore;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SignatureException;
import java.util.List;

import static com.uav.node.demos.crypto.Secp256k.issueCredentialtoGetProof;

@Data
@Component
public class CredentialConfig {

    List<Credential> credentials;//owner credential
    List<Credential> groupCredentials;

    public CredentialConfig()
    {



    }

    public static void main(String[] args) throws Exception {
        //40779086466177057605767635656162985036307673144995806911493908486715184715114

        BigInteger priKey = new BigInteger("40779086466177057605767635656162985036307673144995806911493908486715184715114",10);
        Credential credential = new Credential();
        credential.setType(0);
        credential.setIssuer("did:GCS:2586753285709987093");
        credential.setHolder("did:UAV:4299393459333579452");
        Claim claim = new Claim();
        credential.setClaim(claim);

        String msg = credential.toJson();
        byte[] proof = issueCredentialtoGetProof(priKey,msg);
        credential.setProof(proof);
        System.out.println(credential);

        PersistStore persistStore = new PersistStore();
        persistStore.wirteToFile("leaderCredential","leaderCredential", credential.toJson().getBytes());
        byte[] m = persistStore.loadFromFile("./keystore/leaderCredential.json","leaderCredential");
        Credential credential1 = JsonBytesConverter.fromBytes(m,Credential.class);
        System.out.println(credential);
        System.out.println(credential1);
    }
}
