package com.uav.node.demos.crypto;

import com.uav.node.demos.config.CryptoBean;
import com.uav.node.demos.config.GlobalConfig;
import org.apache.milagro.amcl.BLS381.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.uav.node.demos.crypto.DKGService.hashToG1;


@Component
public class BLSService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Autowired
    CryptoBean cryptoBean;
    private static final ECP2 G2_GENERATOR = ECP2.generator();  // G2群的生成元
    private static final BIG q = new BIG(ROM.CURVE_Order);
    public ECP aggregatedSignatures(HashMap<Integer, ECP> partialSigs) {
        Set<Integer> signers = new HashSet<>();
        for (Map.Entry<Integer, ECP> entry : partialSigs.entrySet()){
            signers.add(entry.getKey());
        }
        System.out.println("partialSigs "+ partialSigs);
        System.out.println("signers "+ signers);

        ECP aggregatedSig = new ECP();
        aggregatedSig.inf();
        for (Map.Entry<Integer, ECP> entry : partialSigs.entrySet()) {
            int signerId = entry.getKey();
            ECP sig = entry.getValue();
            BIG coeff = computeLagrangeCoeff(signers, signerId, q);
            ECP scaled = sig.mul(coeff);
            scaled.affine();
            System.out.println("signerId "+ signerId);
            System.out.println("coeff "+ coeff);
            System.out.println("scaled "+ scaled);
            aggregatedSig.add(scaled);
            aggregatedSig.affine();
        }

        return aggregatedSig;
    }
    private static BIG computeLagrangeCoeff(Set<Integer> signers, int j, BIG order) {
        BIG numerator = new BIG(1);
        BIG denominator = new BIG(1);
        BIG x_j = new BIG(j);

        for (int k : signers) {
            if (k == j) continue;
            BIG x_k = new BIG(k);

            // 分子: Π (0 - x_k)
            BIG term = BIG.modneg(x_k, order);
            numerator = BIG.modmul(numerator, term, order);

            // 分母: Π (x_j - x_k)
            term = modsub(x_j, x_k, order);
            denominator = BIG.modmul(denominator, term, order);
        }

        BIG denomInv = modinv(denominator,order);
        // 打印中间值（调试用）
        System.out.println("signer id: " + j );
        System.out.println("分子: " + numerator );
        System.out.println("分母: " + denominator );
        System.out.println("分母模逆: " + denomInv );
        return BIG.modmul(numerator, denomInv, order);
    }

    public static boolean verifySignature(ECP2 gpk,ECP sig,byte[] data){
        ECP H_m = hashToG1(data);
        FP12 pair1 = PAIR.ate(gpk,H_m);
        FP12 pair1_final = PAIR.fexp(pair1);

        FP12 pair2 = PAIR.ate(G2_GENERATOR, sig);
        FP12 pair2_final = PAIR.fexp(pair2);
        return pair1_final.equals(pair2_final);
    }
    private static BIG modinv(BIG a, BIG q) {
        // 步骤1: 将a转换到模数q的正数范围
        BIG a_mod = new BIG(a);
        a_mod.mod(q); // 强制转换到[0, q-1]范围


        // 步骤3: 计算指数q-2
        BIG exponent = new BIG(q);
        exponent.dec(2); // q -= 2

        // 步骤4: 计算a^(q-2) mod q
        return a_mod.powmod(exponent, q);
    }
    private static BIG modsub(BIG a, BIG b, BIG mod) {
        BIG result = new BIG(a);
        result.sub(b);  // 正确执行 a - b
        result.add(mod); // 防止负数
        result.mod(mod);
        return result;

    }

    public ECP2 computeGk(BIG sk) {
        return G2_GENERATOR.mul(sk);
    }
}