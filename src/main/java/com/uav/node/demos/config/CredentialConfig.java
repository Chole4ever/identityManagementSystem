package com.uav.node.demos.config;

import com.uav.node.demos.model.Credential;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Data
@Component
public class CredentialConfig {

    List<Credential> credentials;
    List<Credential> groupCredentials;

    public CredentialConfig()
    {

    }

    public static void main(String[] args) {
        String hexString = "106606941515692544532094839375866865371314889174551484880372200373727341022272";
        BigInteger bigInteger = new BigInteger(hexString);
        System.out.println(bigInteger);
    }
}
