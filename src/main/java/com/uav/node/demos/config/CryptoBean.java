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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Component
@Scope("singleton")
public class CryptoBean {

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    private int threshold;
    private int n;

    private List<BIG> skshares;
    private List<ECP2> pkshares;

    private BIG[] privateCoeffs;
    private ECP2[] publicCoeffs;
    private BIG sk_i;
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
