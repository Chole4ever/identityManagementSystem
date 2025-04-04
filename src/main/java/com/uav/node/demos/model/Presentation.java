package com.uav.node.demos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static com.uav.node.demos.crypto.Secp256k.signMessage;
import static com.uav.node.demos.crypto.Secp256k.signatureDataToBytes;

@Data
public class Presentation {
    @JsonProperty("@context")
    private List<String> context = Arrays.asList(
            "https://www.w3.org/2018/credentials/v1",
            "https://schema.org"
    );

    private String id;

    private int type;

    private String holder;
    @JsonProperty("issuance_date")
    private String issuanceDate;
    @JsonProperty("expiration_date")
    private String expirationDate;
    @JsonProperty("credential_subject")
    private Credential credentialSubject;
    private byte[] proof;

    public Presentation()
    {
        String id = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        this.setId(id);
        Instant now = Instant.now();
        Instant futureDate = now.plus(Duration.ofDays(30));
        this.setIssuanceDate(String.valueOf(now));
        this.setExpirationDate(String.valueOf(futureDate));
    }

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
    // 反序列化
    public static Presentation fromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Presentation.class);
    }

    public static String getDataAsString(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // String → byte[] (Base64)
    public byte[] setDataFromString(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }

    public void create(Credential leaderCredential, ECKeyPair ecKeyPair,String did) throws JsonProcessingException {

        byte[] msgHash = Hash.sha3(leaderCredential.toJson().getBytes());
        Sign.SignatureData signatureData = signMessage(msgHash,ecKeyPair);
        byte[] signatureDataBytes = signatureDataToBytes(signatureData);

        this.setType(0);
        this.setProof(signatureDataBytes);
        this.setHolder(did);
        this.setCredentialSubject(leaderCredential);
    }
}
