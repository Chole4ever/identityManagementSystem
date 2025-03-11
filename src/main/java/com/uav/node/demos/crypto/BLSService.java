package com.uav.node.demos.crypto;

import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.apache.milagro.amcl.BLS381.ROM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;


@Component
public class BLSService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Autowired
    CryptoBean cryptoBean;
    private static final ECP2 G2_GENERATOR = ECP2.generator();  // G2群的生成元
    private static final BIG q = new BIG(ROM.CURVE_Order);
    private  int t ;
    private  int n ;

    @PostConstruct
    private void init()
    {
        t=config.getThreshold();
        n=config.getCount();
    }
    public ECP aggregatedSignatures(HashMap<Integer, ECP> partialSigs) {
        // 4. 计算Lagrange系数并聚合签名
        ECP aggregatedSig = new ECP();
        aggregatedSig.inf();
        return new ECP();

    }
}