package com.uav.node.demos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;


import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
@Data
public class Credential {
    private String id;
    private int type;
    private String issuer;
    private String holder;
    @JsonProperty("issuance_date")
    private String issuanceDate;
    @JsonProperty("expiration_date")
    private String expirationDate;
    private Claim claim;
    private byte[] proof;

    public String toJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }
    // 反序列化
    public static Credential fromJson(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, Credential.class);
    }

    public static String getDataAsString(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    // String → byte[] (Base64)
    public byte[] setDataFromString(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }

    public static Credential wrapCredential(Claim claim, int type, long validDays, String issuerDID, String holderDID, byte[] proof) throws JsonProcessingException {
        Credential credential = new Credential();
        String id = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        credential.setId(id);
        credential.setType(type);
        credential.setHolder(holderDID);
        credential.setIssuer(issuerDID);
        Instant now = Instant.now();
        Instant futureDate = now.plus(Duration.ofDays(validDays));
        credential.setIssuanceDate(String.valueOf(now));
        credential.setExpirationDate(String.valueOf(futureDate));
        credential.setClaim(claim);
        credential.setProof(proof);
        return credential;
    }


}
