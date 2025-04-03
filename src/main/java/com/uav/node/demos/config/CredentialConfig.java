package com.uav.node.demos.config;

import com.uav.node.demos.model.Credential;
import lombok.Data;
import org.springframework.stereotype.Component;

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

    }
}
