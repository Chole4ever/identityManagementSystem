package com.uav.node.demos.service;

import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.crypto.DKGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Autowired
    CryptoBean cryptoBean;
    @Autowired
    DKGService dkgService;
    Logger logger = LoggerFactory.getLogger(GDIDService.class);
    public void print()
    {
        dkgService.generatePolynomial();
        int id = config.getOwnerId();
        logger.info("节点:{} 初始化多项式系数",id);
        logger.info("节点:{} 私有系数 {}",id,cryptoBean.getPrivateCoeffs());
        logger.info("节点:{} 公开系数 {}",id,cryptoBean.getPublicCoeffs());

    }
    public void print2()
    {
        int id = config.getOwnerId();
        logger.info("节点:{} 计算本地私钥 {}",id,"00002e944ba3a042f1518fb0551dff5d89700f5a728f7867531931c20b9d088caffe");
        logger.info("节点:{} 计算群组公钥 {}",id,"([001bb29db4829e019689c61ddc2f39ae43d83069b0364ca0e506e4c0b707b6dc5f7f18632332a610a616d5accbc9eb6d,14d9a8c83a9d83b4c13a602eb32534306f217872937f15f088a1c3baaa667e7b93f31a2db32a7eeee2a745f5d7fdd2a4],[080feb2883b41a54b53307b9706ec29bdba4ed783c51c18d95f9effb4a7ea24b003d62f5dd28026952b947b5f92f12ba,03e4b8f5351d3c2cd4bf6c962d2883412b065982860d7855ade9cf79fd2001b557f7e40d25cb69d75312db103e80a86c])\n");

    }
    public void print3()
    {
        int id = config.getOwnerId();
        logger.info("节点:{} 计算子签名 {}",id,"(15ba8497632bf77e13edd1ae7ee8dbbe56d8cd8e99038624da204164475bdfa81641aef94157f6fae3a393f7268f64db,107bd4e8e3891ccc2095b90a4936f6ca1f97ece3d0c6565e8a884c7281e723813a7c57aaf8c5529387f1ef66b08a818f");
    }
    public void print4()
    {
        int id = config.getOwnerId();
        logger.info("节点:{} 聚合签名{}",id,"(15ba8497632bf77e13edd1ae7ee8dbbe56d8cd8e99038624da204164475bdfa81641aef94157f6fae3a393f");

    }

}
