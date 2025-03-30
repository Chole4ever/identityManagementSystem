package com.uav.node.demos.crypto;

import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.apache.milagro.amcl.BLS381.ROM;
import org.apache.milagro.amcl.RAND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

@Service
public class DKGService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Autowired
    CryptoBean cryptoBean;
    @Autowired
    BLSService blsService;
    Logger logger = LoggerFactory.getLogger(DKGService.class);
    private static final ECP2 G2_GENERATOR = ECP2.generator();  // G2群的生成元
    private static final BIG q = new BIG(ROM.CURVE_Order);
    private  int t;

    @PostConstruct
    private void init()
    {
        t=config.getThreshold();
    }

    // 1. 生成随机多项式及承诺
    public void generatePolynomial() {

        BIG[] privateCoeffs  = new BIG[t+1];
        ECP2[] publicCoeffs  = new ECP2[t+1];

        String nodeId = "node" + config.getOwnerId(); // 从环境变量或配置中读取每个节点的唯一ID
        long timestamp = System.currentTimeMillis();
        String uniqueSeed = nodeId + timestamp;

        // 将唯一种子转换为字节数组（BIG库通常需要字节输入）
        byte[] seedBytes = uniqueSeed.getBytes();

        RAND rng = new RAND();
        rng.clean(); // 清空初始状态
        rng.seed(seedBytes.length, seedBytes); // 注入唯一种子
        for (int k=0; k<=t; k++) {
            privateCoeffs[k] = BIG.randomnum(q, rng); // a_k ∈ Z_q
            publicCoeffs[k] = G2_GENERATOR.mul(privateCoeffs[k]);   // A_k = a_k * G
        }
        cryptoBean.setPrivateCoeffs(privateCoeffs);
        cryptoBean.setPublicCoeffs(publicCoeffs);
        logger.info("-----------------------------------------------------------------------");
        logger.info("node "+config.getOwnerId()+" generatePolynomial ");
        logger.info("node "+config.getOwnerId()+" privateCoeffs: "+ Arrays.toString(privateCoeffs));
        logger.info("node "+config.getOwnerId()+" publicCoeffs: "+ Arrays.toString(publicCoeffs));
        logger.info("-----------------------------------------------------------------------");
    }

    // 2. 发送份额给其他参与者
    public BIG getSharesById(int targetId) {
        BIG sum = new BIG(0);
        BIG j = new BIG(targetId);

        for (int k = 0; k <= t; k++) {
            BIG jk = j.powmod(new BIG(k), q); // j^k
            BIG term = BIG.modmul(cryptoBean.getPrivateCoeffs()[k], jk, q);
            sum.add(term);
            sum.mod(q);
        }
        return sum;
    }

    public BIG computePrivateKey(List<BIG> shares ) {
        BIG sk_i = new BIG(0);
        for (BIG share : shares){
            sk_i.add(share);
            sk_i.mod(q);
        }

        return sk_i;
    }
    public ECP2 computeGroupPublicKey(List<ECP2> publicCoeffs)
    {
        ECP2 groupPubKey = new ECP2();
        groupPubKey.inf();
        for (ECP2 p : publicCoeffs) {
            groupPubKey.add(p);
        }
        groupPubKey.affine();
        cryptoBean.setGroupPubKey(groupPubKey);
        return groupPubKey;

    }

    public ECP signSig(byte[] metadata) {
        ECP H_m = hashToG1(metadata);
        H_m.affine();
        ECP sig_i = new ECP(H_m);
        sig_i = sig_i.mul(cryptoBean.getSk_i());
        sig_i.affine();
        return sig_i;

    }
    public static ECP hashToG1(byte[] message) {
        byte[] hash;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            hash = digest.digest(message);
        } catch (Exception e) {
            throw new RuntimeException("Hash failed");
        }
        return ECP.mapit(hash);
    }
}




