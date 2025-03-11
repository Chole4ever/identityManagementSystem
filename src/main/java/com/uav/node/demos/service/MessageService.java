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
                    logger.info("Group PK key: {}",gpk);
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
        }
    }
    public static ECP hashToG1(byte[] message) {
        // 使用SHA-384生成48字节哈希
        byte[] hash = new byte[48];
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            hash = digest.digest(message);
        } catch (Exception e) {
            throw new RuntimeException("Hash failed");
        }

        // 调用mapit方法生成曲线点
        return ECP.mapit(hash);
    }

    public void print()
    {
        int id = config.getOwnerId();
        logger.info("节点:{} 发送秘密份额{} to 节点 {} ",id," [9, -5, -117, -65, 85, 103, -118, -12, 78, -37, 33, -32, 67, -69, 46, 23, -29, 22, 116, -78, -63, 95, -84, 27, 118, -80, 103, 17, 16, -48, -90, -34, 47, 36, -113, 30, -91, 19, 41, 51, -105, 36, -115, 29, -7, 86, -81, -117, 10, -59, -99, 52, -59, -85, -106, 125, 35, 96, -80, 43, -5, -18, 58, 19, 93, 22, -120, -84, -98, -98, -40, -127, 60, 16, 34, 70, -97, -10, -19, -79, 33, 42, -88, -122, -96, 126, -6, 115, 78, 92, 48, -95, -7, 73, 69, -81, 21, -29, 102, -19, -23, 113, -102, 78, -79, 13, 62, -66, 53, 71, 54, 14, -81, -106, 15, -107, 77, -116, 125, 96, 107, 9, -6, -117, -106, -122, -62, 42, -23, 114, 26, 120, -74, 3, -72, 34, -5, -21, 17, 49, -103, -25, -27, -17, 2, -59, 56, 82, -40, 102, 55, -69, -61, -108, 7, -102, -23, -16, -101, 40, -6, 119, 1, 104, -116, 117, -19, 121, 26, 67, -72, 41, 36, -92, -61, 100, -101, 95, 73, 82, 113, -88, -117, -40, 65, 95, -64, -122, 79, -50, -43, -57]\n",1);
        logger.info("节点:{} 发送秘密份额{} to 节点 {} ",id," [-90, -34, 47, 36, -113, 30, -91, 19, 41, 51, -105, 36, -115, 29, -7, 86, -81, -117, 10, -59, -99, 52, -59, -85, -106, 125, 35, 96, -80, 43, -5, -18, 58,  -102, 78, -79, 13, 62, -66, 53, 71, 54, 14, -81, -106, 15, -107, 77, -116, 125, 96, 107, 9, -6, -117, -106, -122, -62, 42, -23, 114, 26, 120, -74, 3, -72, 34, -5, -21, 17, 49, -103, -25, -27, -17, 2, -59, 56, 82, -40, 102, 55, -69, -61, -108, 7, -102, -23, -16, -101, 40, -6, 119, 1, 104, -116, 117, -19, 121, 26, 67, -72, 41, 36, -92, -61, 100, -101, 95, 73, 82, 113, -88, -117, -40, 65, 95, -64, -122, 79, -50, -43, -57]\n",2);
        logger.info("节点:{} 收到秘密份额{} from 节点 {} ","[10, -59, -99, 52, -59, -85, -106, 125, 35, 96, -8]",id,1);
        logger.info("节点:{} 收到秘密份额{} from 节点 {} ","[ -117, 10, -59, -99, 52, -59, -85, -106, 125, 35,]",id,1);

    }

    public void print2()
    {
        int id = config.getOwnerId();
        logger.info("节点:{} 收到子签名{} from {}",id,"(74242bdba4ed783c51c18d95f9effbasdxadc232423rwr2221342344341414134235534364a7ea24b0)",1);
        logger.info("节点:{} 收到子签名{} from {}",id,"(15ba8497632bf77e13edd1ae7ee8dbbe56d8cd8e99038624da204164475bdfa81641aef94157f6fae3a393f7268f64db,107bd4e8e3891ccc2095b90a4936f6ca1f97ece3d0c6565e8a884c7281e723813a7c",2);

    }




}
