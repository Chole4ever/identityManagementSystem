package com.uav.node.demos.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BLS{

    // 示例参数（非安全参数，仅用于演示）
    private static final BigInteger q = new BigInteger("7");  // 群的素数阶
    private static final BigInteger p = new BigInteger("101"); // 配对模数
    private static final BigInteger g1 = new BigInteger("5"); // G1生成元
    private static final BigInteger g2 = new BigInteger("5"); // G2生成元

    public static BigInteger hashToBigInteger(byte[] inputBytes) {
        try {
            // 获取 SHA-256 哈希算法的实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 对输入的字节数组进行哈希
            byte[] hashBytes = digest.digest(inputBytes);

            // 将哈希后的字节数组转换为 BigInteger
            BigInteger bigInt = new BigInteger(1, hashBytes); // 参数1表示正数

            return bigInt;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 哈希消息到G2群（示例中简化处理）
    private static BigInteger hashToG2(String message) {
        // 示例中将消息转换为固定值，实际需使用密码学哈希函数
        return g2.multiply(BigInteger.ONE).mod(q); // 模拟H(m) = g2 * 1
    }

    // 模拟双线性配对（示例中简化为乘法）
    private static BigInteger pairing(BigInteger a, BigInteger b) {
        return a.multiply(b).mod(p);
    }

    // 生成私钥（1到q-1之间的随机数）
    public static BigInteger generatePrivateKey() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(q.bitLength(), random)
                .mod(q.subtract(BigInteger.ONE))
                .add(BigInteger.ONE);
    }

    // 从私钥生成公钥（sk * g1 mod q）
    public static BigInteger generatePublicKey(BigInteger privateKey) {
        return g1.multiply(privateKey).mod(q);
    }

    // 对消息签名（sk * H(m) mod q）
    public static BigInteger sign(BigInteger privateKey, String message) {
        BigInteger h = hashToG2(message);
        return h.multiply(privateKey).mod(q);
    }

    // 验证签名（检查e(g1, σ) == e(pk, H(m))）
    public static boolean verify(BigInteger publicKey, String message, BigInteger signature) {
        BigInteger h = hashToG2(message);
        BigInteger e1 = pairing(g1, signature);
        BigInteger e2 = pairing(publicKey, h);
        return e1.equals(e2);
    }

    // 聚合多个签名（签名相加）
    public static BigInteger aggregateSignatures(BigInteger[] signatures) {
        return Arrays.stream(signatures)
                .reduce(BigInteger.ZERO, (a, b) -> a.add(b).mod(q));
    }

    // 聚合多个公钥（公钥相加）
    public static BigInteger aggregatePublicKeys(BigInteger[] publicKeys) {
        return Arrays.stream(publicKeys)
                .reduce(BigInteger.ZERO, (a, b) -> a.add(b).mod(q));
    }

    // 验证聚合签名
    public static boolean verifyAggregate(BigInteger aggPublicKey, String message, BigInteger aggSignature) {
        BigInteger h = hashToG2(message);
        BigInteger e1 = pairing(g1, aggSignature);
        BigInteger e2 = pairing(aggPublicKey, h);
        return e1.equals(e2);
    }

    public static void main(String[] args) {
        // 示例1：单用户签名验证
        BigInteger sk1 = generatePrivateKey();
        BigInteger pk1 = generatePublicKey(sk1);
        String message = "Hello BLS!";

        BigInteger signature = sign(sk1, message);
        boolean isValid = verify(pk1, message, signature);
        System.out.println("单签名验证结果: " + isValid); // 应输出true

        // 示例2：聚合签名验证
        BigInteger sk2 = generatePrivateKey();
        BigInteger pk2 = generatePublicKey(sk2);

        // 生成两个签名
        BigInteger sig1 = sign(sk1, message);
        BigInteger sig2 = sign(sk2, message);

        // 聚合签名和公钥
        BigInteger aggSig = aggregateSignatures(new BigInteger[]{sig1, sig2});
        BigInteger aggPub = aggregatePublicKeys(new BigInteger[]{pk1, pk2});

        // 验证聚合签名
        boolean isAggValid = verifyAggregate(aggPub, message, aggSig);
        System.out.println("聚合签名验证结果: " + isAggValid); // 应输出true
    }
}