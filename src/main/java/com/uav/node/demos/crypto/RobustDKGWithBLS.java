package com.uav.node.demos.crypto;


import org.apache.milagro.amcl.BLS381.*;
import org.apache.milagro.amcl.RAND;
import java.security.MessageDigest;
import java.util.*;

public class RobustDKGWithBLS {
    private static final int n = 6;   // 总参与方数
    private static final int t = 2;   // 门限值 (必须满足 n >= 2t+1)
    private static final ECP2 G2_GEN = ECP2.generator(); // G2群生成元
    private static final BIG CURVE_ORDER = new BIG(ROM.CURVE_Order);


    static class Participant {
        int id;
        BIG[] secretCoeffs;    // 私密多项式系数 [a0, a1,...,at] (长度t+1)
        ECP2[] publicCommits; // 公开承诺 [A0, A1,...,At]
        Map<Integer, BIG> sharesReceived = new HashMap<>(); // 收到的秘密份额
        BIG secretShare;       // 最终的私钥份额 sk_i = Σ s_ji

        public Participant(int id) {
            this.id = id;
            generatePolynomial();
        }

        // 生成t次多项式（t+1个系数）
        private void generatePolynomial() {
            secretCoeffs = new BIG[t + 1];
            publicCommits = new ECP2[t + 1];

            RAND rng = new RAND();
            // 混合系统时间和参与者ID作为种子
            byte[] seed = (System.currentTimeMillis() + "_" + this.id).getBytes();
            rng.seed(seed.length,seed);
            for (int k = 0; k <= t; k++) {
                secretCoeffs[k] = BIG.randomnum(CURVE_ORDER, rng);
                publicCommits[k] = G2_GEN.mul(secretCoeffs[k]); // A_k = a_k*G
            }
            System.out.println("id "+id+"\nsecretCoeffs: "+ Arrays.toString(secretCoeffs) +"\npublicCommits: "+ Arrays.toString(publicCommits));

        }

        // 给其他参与者分发秘密份额
        public void distributeShares(List<Participant> participants) {
            for (Participant p : participants) {
                BIG share = computeShareFor(p.id);
                p.receiveShare(this.id, share);
            }
        }

        // 计算给目标参与者的份额 s_ij = P(j) = Σ a_k * j^k
        private BIG computeShareFor(int targetId) {
            BIG sum = new BIG(0);
            BIG j = new BIG(targetId);

            for (int k = 0; k <= t; k++) {
                BIG jk = j.powmod(new BIG(k), CURVE_ORDER); // j^k
                BIG term = BIG.modmul(secretCoeffs[k], jk, CURVE_ORDER);
                sum.add(term);
                sum.mod(CURVE_ORDER);
            }
            return sum;
        }

        // 接收并存储来自其他参与者的份额
        public void receiveShare(int senderId, BIG share) {
            sharesReceived.put(senderId, share);
        }

        // 验证所有收到的份额
        public boolean verifyShares(Map<Integer, ECP2[]> allCommits) {
            for (Map.Entry<Integer, BIG> entry : sharesReceived.entrySet()) {
                int senderId = entry.getKey();
                BIG s_ij = entry.getValue();
                ECP2[] senderCommits = allCommits.get(senderId);

                // 计算右侧: Σ (A_k * j^k)
                ECP2 rhs = new ECP2();
                rhs.inf();
                BIG j = new BIG(this.id);

                for (int k = 0; k <= t; k++) {
                    BIG jk = j.powmod(new BIG(k), CURVE_ORDER);
                    ECP2 term = senderCommits[k].mul(jk);
                    rhs.add(term);
                }
                rhs.affine();

                // 计算左侧: s_ij * G
                ECP2 lhs = G2_GEN.mul(s_ij);
                lhs.affine();

                if (!lhs.equals(rhs)) {
                    System.err.printf("Participant %d 验证失败来自 %d\n", this.id, senderId);
                    return false;
                }
            }
            return true;
        }

        // 计算最终私钥份额
        public void computeSecretShare() {
            secretShare = new BIG(0);
            for (BIG share : sharesReceived.values()) {
                secretShare.add(share);
                secretShare.mod(CURVE_ORDER);
            }
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

    // 生成群公钥
    private static ECP2 computeGroupKey(List<Participant> participants) {
        ECP2 groupKey = new ECP2();
        groupKey.inf();
        for (Participant p : participants) {
            groupKey.add(p.publicCommits[0]); // 所有A_0的和
        }
        groupKey.affine();
        return groupKey;
    }

    // BLS签名聚合
    public static ECP aggregateSignatures(Map<Integer, ECP> sigs) {
        ECP aggregated = new ECP();
        aggregated.inf();
        for (ECP sig : sigs.values()) {
            aggregated.add(sig);
            aggregated.affine();
        }
        return aggregated;
    }

    // 拉格朗日系数计算
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

    public static void main(String[] args) {
        // 初始化所有参与者
        List<Participant> participants = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            participants.add(new Participant(i));
        }

        // 收集所有公开承诺
        Map<Integer, ECP2[]> allCommits = new HashMap<>();
        for (Participant p : participants) {
            allCommits.put(p.id, p.publicCommits);
        }

        // 分发份额
        for (Participant p : participants) {
            p.distributeShares(participants);
        }

        // 验证阶段
        boolean allValid = participants.stream().allMatch(p -> p.verifyShares(allCommits));
        if (!allValid) {
            System.out.println("存在无效份额，终止流程");
            return;
        }

        // 计算群公钥
        ECP2 groupPubKey = computeGroupKey(participants);
        System.out.println("群公钥生成成功: " + groupPubKey.toString());

        // 计算各参与者的私钥份额
        participants.forEach(Participant::computeSecretShare);

        for(Participant p :participants)
        {
            System.out.println("id "+p.id+" sk "+p.secretShare);
        }

        // 门限签名演示
        byte[] message = "Test message".getBytes();
        ECP H_m = hashToG1(message);
        H_m.affine();

        // 收集t+1个签名
        Map<Integer, ECP> partialSigs = new HashMap<>();
        Set<Integer> signers = new HashSet<>();
        for (int i = 0; i < t + 1; i++) {
            Participant signer = participants.get(i);
            ECP sig = H_m.mul(signer.secretShare);
            sig.affine();
            partialSigs.put(signer.id, sig);
            signers.add(signer.id);
        }
        System.out.println("partialSigs "+partialSigs);

        // 聚合签名
        ECP aggregatedSig = new ECP();
        aggregatedSig.inf();
        for (Map.Entry<Integer, ECP> entry : partialSigs.entrySet()) {
            int signerId = entry.getKey();
            ECP sig = entry.getValue();

            // 计算拉格朗日系数
            BIG coeff = computeLagrangeCoeff(signers, signerId, CURVE_ORDER);
            System.out.println("signerId "+signerId+" coeff "+coeff);
            // 应用系数到签名
            ECP scaled = sig.mul(coeff);
            scaled.affine();

            aggregatedSig.add(scaled);
            aggregatedSig.affine();
        }

        // 验证聚合签名
        FP12 pair1 = PAIR.ate(groupPubKey, H_m);
        FP12 pair1_final = PAIR.fexp(pair1);

        FP12 pair2 = PAIR.ate(G2_GEN, aggregatedSig);
        FP12 pair2_final = PAIR.fexp(pair2);

        System.out.println("验证结果: " + (pair1_final.equals(pair2_final) ? "成功" : "失败"));
    }
}
