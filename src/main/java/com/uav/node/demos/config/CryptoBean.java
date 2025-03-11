package com.uav.node.demos.config;



import lombok.Data;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@Scope("singleton") // 明确指定该Bean为单例
public class CryptoBean {

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    private int threshold;
    private int n;

    private List<BIG> skshares;
    private List<ECP2> pkshares;

    private BIG[] privateCoeffs;          // 私密多项式系数 [a0, a1, ..., at]
    private ECP2[] publicCoeffs;          // 公共承诺系数 [A0, A1, ..., At]
    private BIG sk_i;  // 新增：子私钥 sk_i = sum(s_ji) mod q，j ∈ QUAL
    private ECP2 groupPubKey;
    private HashMap<Integer,ECP> partialSigs;

    @PostConstruct
    public void init() {
        threshold = config.getThreshold();
        n = config.count;
        skshares = new ArrayList<>();
        pkshares = new ArrayList<>();
        partialSigs = new HashMap<>();
    }
}

/*
    static final BigInteger q = new BigInteger("123456789"); // Modulus for the field
    static final BigInteger p = new BigInteger("987654321"); // Prime modulus for group operations
    static final BigInteger generator = new BigInteger("2"); // Generator value

 */
