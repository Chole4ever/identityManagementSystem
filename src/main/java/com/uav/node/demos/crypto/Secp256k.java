package com.uav.node.demos.crypto;

import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.web3j.crypto.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.SignatureException;

public class Secp256k {

    // 生成一个随机的私钥
    public static BigInteger generateRandomPrivKey() {
        SecureRandom secureRandom = new SecureRandom();
        BigInteger privKey = new BigInteger(256, secureRandom);  // 生成一个256位的私钥
        // 确保私钥在有效范围内，即私钥小于secp256k1曲线的阶
        BigInteger order = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        return privKey.mod(order);
    }

    // 生成公私钥对
    public static ECKeyPair generateKeyPair(BigInteger privKey) {
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        return new ECKeyPair(privKey, pubKey);
    }

    public static ECKeyPair generateKeyPair() {
        BigInteger privKey = generateRandomPrivKey();
        BigInteger pubKey = Sign.publicKeyFromPrivate(privKey);
        return new ECKeyPair(privKey, pubKey);
    }

    // 压缩公钥
    public static String compressPubKey(BigInteger pubKey) {
        String pubKeyYPrefix = pubKey.testBit(0) ? "03" : "02";
        String pubKeyHex = pubKey.toString(16);
        String pubKeyX = pubKeyHex.substring(0, 64);
        return pubKeyYPrefix + pubKeyX;
    }

    // 对消息进行签名
    public static Sign.SignatureData signMessage(byte[] msgHash, ECKeyPair keyPair) {
        return Sign.signMessage(msgHash, keyPair, false);
    }

    // 验证签名
    public static boolean verifySignature(String msg, Sign.SignatureData signature, BigInteger pubKey) throws SignatureException {
        BigInteger pubKeyRecovered = Sign.signedMessageToKey(msg.getBytes(), signature);
        return pubKey.equals(pubKeyRecovered);
    }

    // 主函数
    public static void main(String[] args) throws Exception {
        // 随机生成私钥
        BigInteger privKey = generateRandomPrivKey();
        System.out.println("Generated private key: " + privKey.toString(16));

        // 生成密钥对
        ECKeyPair keyPair = generateKeyPair(privKey);
        System.out.println("Public key: " + keyPair.getPublicKey().toString(16));
        System.out.println("Public key (compressed): " + compressPubKey(keyPair.getPublicKey()));

        // 签名操作
        String msg = "Message for signing";
        byte[] msgHash = Hash.sha3(msg.getBytes());
        Sign.SignatureData signature = signMessage(msgHash, keyPair);
        System.out.println("Msg: " + msg);
        System.out.println("Msg hash: " + Numeric.toHexString(msgHash));
        System.out.printf("Signature: [v = %d, r = %s, s = %s]\n",
                signature.getV() - 27,
                Numeric.toHexString(signature.getR()),
                Numeric.toHexString(signature.getS()));

        // 验证签名
        boolean validSig = verifySignature(msg, signature, keyPair.getPublicKey());
        System.out.println("Signature valid? " + validSig);
    }
}
