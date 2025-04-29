package com.uav.node.demos.config;


import com.uav.node.demos.crypto.Secp256k;
import com.uav.node.demos.model.Claim;
import com.uav.node.demos.model.Credential;
import com.uav.node.demos.util.JsonBytesConverter;
import com.uav.node.demos.util.PersistStore;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.uav.node.demos.crypto.Secp256k.*;

@Data
@Component
public class CredentialConfig {

    List<Credential> credentials;//owner credential
    List<Credential> groupCredentials;

    public CredentialConfig() throws Exception {
        PersistStore persistStore = new PersistStore();
        byte[] m = persistStore.loadFromFile("./keystore/leaderCredential.json","leaderCredential");
        if(m!=null)
        {
            Credential credential = JsonBytesConverter.fromBytes(m,Credential.class);
            this.credentials = new ArrayList<>();
            this.credentials.add(credential);
        }

        byte[] x = persistStore.loadFromFile("./keystore/GroupCredential.json","GroupCredential");
        if(x!=null)
        {
            Credential credential2 = JsonBytesConverter.fromBytes(x,Credential.class);
            this.groupCredentials = new ArrayList<>();
            this.groupCredentials.add(credential2);
        }


    }
}
