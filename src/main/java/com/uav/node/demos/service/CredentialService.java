package com.uav.node.demos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.*;
import com.uav.node.demos.network.UDPClient;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Sign;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SignatureException;

import static com.uav.node.demos.crypto.BLSService.verifyBLSSignature;
import static com.uav.node.demos.crypto.Secp256k.*;

@Service
public class CredentialService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    @Autowired
    SmartContractService smartContractService;
    Logger logger = LoggerFactory.getLogger(CredentialService.class);

    public boolean verifyCredential(Credential credential) throws IOException, TransactionBaseException, ContractCodecException, SignatureException {
        String did = credential.getIssuer();
        int type = credential.getType();//type0:单点
        byte[] proof = credential.getProof();
        Claim claim = credential.getClaim();
        if(type==0)
        {
            DDO ddo = smartContractService.findDID(did);
            String[] pkList = ddo.getPublicKeys();
            String pkString = pkList[0];
            BigInteger pk = new BigInteger(pkString) ;
            Sign.SignatureData signatureData = signatureDataFromBytes(proof);

            String groupName = config.getGroupName()+"-";

            if(verifySignature(claim.toJson(),signatureData,pk)){
                logger.info(groupName+"node {} verifies credential true, credential {}",config.getOwnerId(),credential.toJson());
                return true;
            }else{
                logger.info(groupName+"node {} verifies credential false, credential {}",config.getOwnerId(),credential.toJson());
            }
        }else if(type==1)
        {
            GDDO gddo = smartContractService.findGDID(did);
            byte[] pkList = gddo.getPublicKeys();
            ECP2 pk = ECP2.fromBytes(pkList);
            ECP signatureData = ECP.fromBytes(proof);

            if(verifyBLSSignature(pk,signatureData,claim.toJson().getBytes()))
            {
                logger.info("node {} verifies credential true, credential {}",config.getOwnerId(),credential.toJson());
                return true;
            }else{
                logger.info("node {} verifies credential true, credential {}",config.getOwnerId(),credential.toJson());
            }
        }

        return false;
    }

    public boolean verifyPresentation(Presentation presentation) throws TransactionBaseException, ContractCodecException, IOException, SignatureException {
        byte[] proof = presentation.getProof();
        int type = presentation.getType();
        String did = presentation.getHolder();

        if(type==0)
        {
            DDO ddo = smartContractService.findDID(did);
            String[] pkList = ddo.getPublicKeys();
            String pkString = pkList[0];
            BigInteger pk = new BigInteger(pkString) ;
            Sign.SignatureData signatureData = signatureDataFromBytes(proof);
            if(verifySignature(presentation.getCredentialSubject().toJson(),signatureData,pk)){
                logger.info("node {} verifies presentation true, presentation {}",config.getOwnerId(),presentation.toJson());
                return true;
            }else{
                logger.info("node {} verifies presentation false, presentation {}",config.getOwnerId(),presentation.toJson());
            }
        }else{

            GDDO gddo = smartContractService.findGDID(did);
            byte[] pkList = gddo.getPublicKeys();
            ECP2 pk = ECP2.fromBytes(pkList);
            ECP signatureData = ECP.fromBytes(proof);
            Credential credential = presentation.getCredentialSubject();

            if(verifyBLSSignature(pk,signatureData,credential.toJson().getBytes()))
            {
                logger.info("node {} verifies credential true, credential {}",config.getOwnerId(),presentation.toJson());
                return true;
            }else{
                logger.info("node {} verifies credential true, credential {}",config.getOwnerId(),presentation.toJson());
            }
        }
       return false;
    }
}
