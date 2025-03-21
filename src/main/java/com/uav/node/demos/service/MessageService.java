package com.uav.node.demos.service;

import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.BLSService;
import com.uav.node.demos.crypto.DKGService;
import com.uav.node.demos.model.Message;
import org.apache.commons.math3.FieldElement;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
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
                    logger.info("node {} calculate sub key...",config.getOwnerId());
                    BIG sk = dkgService.computePrivateKey(skshares);
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
                    logger.info("node {} calculate PK key...",config.getOwnerId());
                    ECP2 gpk = dkgService.computeGroupPublicKey(pkshares);
                    logger.info("Group PK key: {}",gpk.toString());
                    gdidService.launchGdidRR();
                }
                break;
            case "BROADG_DID_GENERATION_REQUEST":

                byte[] metadata = message.getValue();
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
                  int from = message.getFromId();
                  HashMap<Integer, ECP> partialSigs = cryptoBean.getPartialSigs();
                  partialSigs.put(from,sig_i);
                  cryptoBean.setPartialSigs(partialSigs);
                if(partialSigs.size()==config.getThreshold()) {
                    logger.info("node {} calculate agg sig...",config.getOwnerId());
                    ECP agg = blsService.aggregatedSignatures(partialSigs);
                    logger.info("node {} generates agg sig {} ",config.getOwnerId(),agg);
                    gdidService.sendRRToSc(agg);
                }

                break;
            default:
                logger.info("Unknown message type: " + message.getCommand());
                break;
        }
    }

}
