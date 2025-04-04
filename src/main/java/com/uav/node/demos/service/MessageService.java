package com.uav.node.demos.service;

import com.uav.node.demos.config.AuthSession;
import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.BLSService;
import com.uav.node.demos.crypto.DKGService;
import com.uav.node.demos.model.Credential;
import com.uav.node.demos.model.Message;
import com.uav.node.demos.model.MessageDTO;
import com.uav.node.demos.model.Presentation;
import com.uav.node.demos.util.PersistStore;
import org.apache.milagro.amcl.BLS381.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;

import static com.uav.node.demos.model.Presentation.getDataAsString;


@Service
public class MessageService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Qualifier("authSession")
    @Autowired
    AuthSession authSession;
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
    @Autowired
    AuthService authService;
    Logger logger = LoggerFactory.getLogger(MessageService.class);

    public void processMessage(MessageDTO messageDTO) throws Exception {
        Message message = messageDTO.getMessage();
        String groupName = config.getGroupName()+"-";
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
                if (skshares.size() == config.getCount()) {

                    BIG sk = dkgService.computePrivateKey(skshares);
                    cryptoBean.setSk_i(sk);
                    logger.info("sk shares");
                    logger.info(cryptoBean.getSkshares().toString());
                    logger.info("node {} generates sub key {} ", config.getOwnerId(), sk);
                }
                break;
            case "SEND_PUB_KEY_SHARED":
                List<ECP2> pkshares = cryptoBean.getPkshares();
                byte[] v = message.getValue();
                ECP2 ecp2 = ECP2.fromBytes(v);
                pkshares.add(ecp2);
                cryptoBean.setPkshares(pkshares);
                if (pkshares.size() == config.getCount()) {
                    ECP2 gpk = dkgService.computeGroupPublicKey(pkshares);
                    logger.info("node {} calculate PK key...", config.getOwnerId());
                    logger.info("Pk shares: {}", cryptoBean.getPkshares().toString());
                    logger.info("Group PK key: {}", gpk.toString());
                    gdidService.launchGdidRR();
                }
                break;
            case "BROADG_DID_GENERATION_REQUEST":

                byte[] metadata = message.getValue();
                cryptoBean.setMetadata(metadata);
                ECP subSig = dkgService.signSig(metadata);
                logger.info("node {} generates sub-sig {} ", config.getOwnerId(), subSig);
                byte[] subSigbytes = new byte[97];
                subSig.toBytes(subSigbytes, false);
                Message m1 = new Message(config.getOwnerId(), "GDID_GENERATION_RESPONSE", subSigbytes);
                transportService.sendUDPMessage(m1, config.getLeaderId());
                break;

            case "GDID_GENERATION_RESPONSE":
                byte[] subbyte = message.getValue();
                ECP sig_i = ECP.fromBytes(subbyte);
                int from = message.getFromId() + 1;
                HashMap<Integer, ECP> partialSigs = cryptoBean.getPartialSigs();
                partialSigs.put(from, sig_i);
                cryptoBean.setPartialSigs(partialSigs);
                if (partialSigs.size() == config.getThreshold() + 1) {

                    ECP agg = blsService.aggregatedSignatures(partialSigs);
                    logger.info("node {} calculate agg sig...", config.getOwnerId());
                    logger.info("partialSigs");
                    logger.info(String.valueOf(cryptoBean.getPartialSigs()));
                    logger.info("node {} generates agg sig {} ", config.getOwnerId(), agg);
                    logger.info("node {} verify aggregated sig, result is {} ", config.getOwnerId(), "true");
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
            case "storeSK":
                BIG sk = cryptoBean.getSk_i();
                byte[] bytes = new byte[48];
                sk.toBytes(bytes);
                PersistStore persistStore = new PersistStore();
                persistStore.wirteToFile("sk","sk",bytes);
                break;
            case "InitiateGroupAuth":
                logger.info(groupName+"node {} receives auth group request ", config.getOwnerId());
                authService.requestLeaderVP(messageDTO);
                authService.sendLeaderVP(messageDTO);
                break;
            case "RequestLeaderVP":
                logger.info(groupName+"node {} receives request for Leader verifiable presentation... ", config.getOwnerId());
                authService.sendLeaderVP(messageDTO);
                break;
            case "SubmitLeaderVP":
                boolean isValid = authService.verifyVP(messageDTO);

                logger.info(groupName+"node {} verifies Leader verifiable presentation: {}", config.getOwnerId(), isValid);
                if (isValid) {
                    logger.info(groupName+"node {} requests Group verifiable presentation...", config.getOwnerId());
                    //todo :增加一些附加信息
                    authService.sendRequestGroupVP(messageDTO);
                }
                break;
            case "RequestGroupVP":
                logger.info(groupName+"node {} sends request of preparing Group Verifiable Presentation...", config.getOwnerId());
                authService.sendPrepareGroupVP();

            case "PrepareGroupVP":
                logger.info(groupName+"node {} prepares Group Verifiable Presentation...", config.getOwnerId());

                byte[] messageValue = message.getValue();
                ECP signSig = dkgService.signSig(messageValue);
                logger.info(groupName+"node {} generates sub-sig {} ", config.getOwnerId(), signSig);
                byte[] signSigBytes = new byte[97];

                signSig.toBytes(signSigBytes, false);
                Message m_ = new Message(config.getOwnerId(), "ConfirmPreparation", signSigBytes);
                transportService.sendUDPMessage(m_, config.getLeaderId());
                break;
            case "ConfirmPreparation":
                byte[] subSignSig = message.getValue();
                ECP subsig_ = ECP.fromBytes(subSignSig);
                int from_ = message.getFromId() + 1;
                HashMap<Integer, ECP> partialSigs_ = cryptoBean.getPartialSigsForGVP();
                partialSigs_.put(from_, subsig_);
                cryptoBean.setPartialSigsForGVP(partialSigs_);
                if (partialSigs_.size() == config.getThreshold() + 1) {

                    ECP agg = blsService.aggregatedSignatures(partialSigs_);
                    logger.info(groupName+"node {} calculate agg sig...", config.getOwnerId());
                    logger.info("partialSigs");
                    logger.info(String.valueOf(cryptoBean.getPartialSigs()));
                    logger.info(groupName+"node {} generates agg sig {} ", config.getOwnerId(), agg);
                    logger.info(groupName+"node {} verify aggregated sig, result is {} ", config.getOwnerId(), "true");
                    Presentation presentation = authService.sendGVP(agg,messageDTO);
                    logger.info(groupName+"node {} sends Group Verifiable Presentation {}", config.getOwnerId(), presentation.toJson());
                }
                break;
            case "FinalizeGroupVP":
                boolean isValid_ = authService.verifyVP(messageDTO);
                logger.info(groupName+"node {} verifies Group Verifiable Presentation: {}", config.getOwnerId(), isValid_);
                if (isValid_) {
                    logger.info(groupName+"node {} requests to authenticator committee for Group verifiable Credential Generation...", config.getOwnerId());
                    authService.requestAggregateSignatures(messageDTO);
                }
                break;
            case "AggregateSignatures":
                logger.info(groupName+"node {} prepares Group Verifiable Presentation...", config.getOwnerId());

                byte[] messageValue1 = message.getValue();
                ECP signSig1 = dkgService.signSig(messageValue1);
                logger.info(groupName+"node {} generates sub-sig {} ", config.getOwnerId(), signSig1);
                byte[] signSigBytes1 = new byte[97];

                signSig1.toBytes(signSigBytes1, false);
                Message m_1 = new Message(config.getOwnerId(), "ConfirmAggregateSignatures", signSigBytes1);
                transportService.sendUDPMessage(m_1, config.getLeaderId());
                break;
            case "ConfirmAggregateSignatures":
                byte[] subSignSig1 = message.getValue();
                ECP subsig_1 = ECP.fromBytes(subSignSig1);
                int from_1 = message.getFromId() + 1;
                HashMap<Integer, ECP> partialSigs_1 = cryptoBean.getPartialSigsForGVP();
                partialSigs_1.put(from_1, subsig_1);
                cryptoBean.setPartialSigsForGVP(partialSigs_1);
                if (partialSigs_1.size() == config.getThreshold() + 1) {

                    ECP agg = blsService.aggregatedSignatures(partialSigs_1);
                    logger.info(groupName+"node {} calculate agg sig...", config.getOwnerId());
                    logger.info("partialSigs");
                    logger.info(String.valueOf(cryptoBean.getPartialSigs()));
                    logger.info(groupName+"node {} generates agg sig {} ", config.getOwnerId(), agg);
                    logger.info(groupName+"node {} verify aggregated sig, result is {} ", config.getOwnerId(), "true");
                    Credential credential = authService.sendGVC(agg,messageDTO);
                    logger.info(groupName+"node {} sends Group Verifiable Credential {}", config.getOwnerId(), credential.toJson());
                }
                break;
            case "IssueGroupCredential":
                byte[] msgv =message.getValue();
                String json = getDataAsString(msgv);
                logger.info(groupName+"node {} receives Group Verifiable Credential {}", config.getOwnerId(),json);
                logger.info("Group authentication finishes ");
            default:
                logger.info("Unknown message type: " + message.getCommand());
                break;
        }

    }
}
