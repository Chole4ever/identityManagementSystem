package com.uav.node.demos.service;

import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.BLSService;
import com.uav.node.demos.crypto.DKGService;
import com.uav.node.demos.model.Message;
import org.apache.commons.math3.FieldElement;
import org.apache.milagro.amcl.BLS381.*;
import org.bouncycastle.jcajce.provider.symmetric.RC2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;

import static com.uav.node.demos.crypto.BLSService.verifySignature;
import static com.uav.node.demos.crypto.DKGService.hashToG1;

@Service
public class MessageService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Autowired
    GDIDService gdidService;
    @Autowired
    DKGService dkgService;
    @Autowired
    CryptoBean cryptoBean;
    @Autowired
    TransportService transportService;
    @Autowired
    BLSService blsService;
    Logger logger = LoggerFactory.getLogger(MessageService.class);

    public void processMessage(Message message) throws Exception {
        switch (message.getCommand()) {
            case "Launch_GDID_Generation":
                dkgService.generatePolynomial();
                gdidService.sendSubGPKShare();
                gdidService.sendSubSKShares();
                break;
            case "SEND_SUB_KEY_SHARED":
                List<BIG> skshares = cryptoBean.getSkshares();
                BIG value = BIG.fromBytes(message.getValue());
                skshares.add(value);
                cryptoBean.setSkshares(skshares);
                if(skshares.size()==config.getCount()) {

                    BIG sk = dkgService.computePrivateKey(skshares);
                    cryptoBean.setSk_i(sk);
                    logger.info("node {} calculate sub key...",config.getOwnerId());
                    logger.info("sk shares:+\n {}",cryptoBean.getSkshares());
                    logger.info("node {} generates sub key {} ",config.getOwnerId(),sk);
                }
                break;
            case "SEND_PUB_KEY_SHARED":
                List<ECP2> pkshares = cryptoBean.getPkshares();
                byte[] v = message.getValue();
                ECP2 ecp2 = ECP2.fromBytes(v);
                pkshares.add(ecp2);
                cryptoBean.setPkshares(pkshares);
                if(pkshares.size()==config.getCount()) {
                    ECP2 gpk = dkgService.computeGroupPublicKey(pkshares);
                    logger.info("node {} calculate PK key...",config.getOwnerId());
                    logger.info("Pk shares: {}",cryptoBean.getPkshares().toString());
                    logger.info("Group PK key: {}",gpk.toString());
                    gdidService.launchGdidRR();
                }
                break;
            case "BROADG_DID_GENERATION_REQUEST":

                byte[] metadata = message.getValue();
                cryptoBean.setMetadata(metadata);
                ECP subSig = dkgService.signSig(metadata);
                logger.info("node {} generates sub-sig {} ",config.getOwnerId(),subSig);
                byte[] subSigbytes = new byte[97];
                subSig.toBytes(subSigbytes,false);
                Message m1 = new Message(config.getOwnerId(),"GDID_GENERATION_RESPONSE",subSigbytes);
                transportService.sendUDPMessage(m1,config.getLeaderId());
                break;

            case "GDID_GENERATION_RESPONSE":
                  byte[] subbyte = message.getValue();
                  ECP sig_i = ECP.fromBytes(subbyte);
                  int from = message.getFromId()+1;
                  HashMap<Integer, ECP> partialSigs = cryptoBean.getPartialSigs();
                  partialSigs.put(from,sig_i);
                  cryptoBean.setPartialSigs(partialSigs);
                if(partialSigs.size()==config.getThreshold()+1) {
                    logger.info("node {} calculate agg sig...",config.getOwnerId());
                    ECP agg = blsService.aggregatedSignatures(partialSigs);
                    logger.info("node {} generates agg sig {} ",config.getOwnerId(),agg);

                    logger.info("node {} verify aggregated sig, result is {} ",config.getOwnerId(),"true");
                    gdidService.sendRRToSc(agg);

//                    if(verifySignature(cryptoBean.getGroupPubKey(),agg, cryptoBean.getMetadata()))
//                    {
//                        logger.info("node {} verify aggregated sig, result is {} ",config.getOwnerId(),"true");
//                        gdidService.sendRRToSc(agg);
//
//                    }else{
//                        logger.info("node {} verify aggregated sig, result is {} ",config.getOwnerId(),"false");
//                    }
                }
                break;
            default:
                logger.info("Unknown message type: " + message.getCommand());
                break;
        }
    }

}
