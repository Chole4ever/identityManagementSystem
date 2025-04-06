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
    private int type;// type0:单点,1群组
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

    public Credential()
    {
        String id = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        this.setId(id);

        Instant now = Instant.now();
        this.setIssuanceDate(String.valueOf(now));

        Instant futureDate = now.plus(Duration.ofDays(30));
        this.setExpirationDate(String.valueOf(futureDate));

    }

    public static Credential wrapCredential(Claim claim, int type, long validDays, String issuerDID, String holderDID, byte[] proof) throws JsonProcessingException {
        Credential credential = new Credential();

        credential.setType(type);
        credential.setHolder(holderDID);
        credential.setIssuer(issuerDID);

        Instant now = Instant.now();
        credential.setIssuanceDate(String.valueOf(now));

        Instant futureDate = now.plus(Duration.ofDays(validDays));

        credential.setExpirationDate(String.valueOf(futureDate));
        credential.setClaim(claim);
        credential.setProof(proof);
        return credential;
    }


}
