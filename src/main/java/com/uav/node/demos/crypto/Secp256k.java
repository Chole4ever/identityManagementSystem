package com.uav.node.demos.crypto;

import com.uav.node.demos.model.Credential;
import org.fisco.bcos.sdk.v3.utils.Numeric;
import org.web3j.crypto.*;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.util.Arrays;

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
        System.out.println("msgHash    "+msg);
        return pubKey.equals(pubKeyRecovered);
    }

    public static byte[] issueCredentialtoGetProof(BigInteger privKey,String msg) throws SignatureException {
        ECKeyPair keyPair = generateKeyPair(privKey);
        System.out.println("Private key: " + privKey);
        System.out.println("Public key: " + keyPair.getPublicKey());

        // 签名操作
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
        if(validSig)
        {
            return signatureDataToBytes(signature);
        }else return new byte[0];
    }

    // 主函数


    public static byte[] signatureDataToBytes(Sign.SignatureData signatureData) {
        // 验证r/s长度符合预期
        if (signatureData.getR().length != 32 || signatureData.getS().length != 32) {
            throw new IllegalStateException("Invalid signature length: r="+signatureData.getR().length+" s="+signatureData.getS().length);
        }

        byte[] result = new byte[65];
        result[0] = signatureData.getV(); // 第1字节是v
        System.arraycopy(signatureData.getR(), 0, result, 1, 32);  // 第2-33字节是r
        System.arraycopy(signatureData.getS(), 0, result, 33, 32); // 第34-65字节是s
        return result;
    }

    // 从字节数组解码（必须严格65字节）
    public static Sign.SignatureData signatureDataFromBytes(byte[] bytes) {
        if (bytes.length != 65) {
            throw new IllegalArgumentException(
                    "Invalid encoded signature: length=" + bytes.length + " (expected 65)"
            );
        }

        byte v = bytes[0];
        byte[] r = Arrays.copyOfRange(bytes, 1, 33);  // 截取1-32字节
        byte[] s = Arrays.copyOfRange(bytes, 33, 65); // 截取33-64字节

        // 可选：二次验证长度
        if (r.length != 32 || s.length != 32) {
            throw new IllegalArgumentException("Corrupted data: r/s length invalid");
        }

        return new Sign.SignatureData(v, r, s);
    }

    public static void main(String[] args) throws Exception {
        // 随机生成私钥
        BigInteger privKey = new BigInteger("40779086466177057605767635656162985036307673144995806911493908486715184715114",10);
        System.out.println("Generated private key: " + privKey);

        // 生成密钥对
        ECKeyPair keyPair = generateKeyPair(privKey);
        System.out.println("Public key: " + keyPair.getPublicKey());

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
