package com.uav.node.demos.crypto;



import com.uav.node.demos.model.Message;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class DKG {
    private int threshold;
    private int n;

    private BigInteger sk;
    private List<BigInteger> A_values;
    private List<BigInteger> S_values;
    private List<BigInteger> coefficients;

    static final BigInteger q = new BigInteger("123456789"); // Modulus for the field
    static final BigInteger p = new BigInteger("987654321"); // Prime modulus for group operations
    static final BigInteger generator = new BigInteger("2"); // Generator value

    public void initDKG(int t,int count) {
        this.threshold = t;
        this.n = count;

        this.coefficients = new ArrayList<>();
        this.A_values = new ArrayList<>();
        this.S_values = new ArrayList<>();

        this.coefficients = selectPolynomial();
        this.A_values = computeAValues();
        this.S_values = computeShares();

    }
    public List<BigInteger> CaculateSubSKShared(){return S_values;};
    public BigInteger CaculateSubGPKShared(){
        return A_values.get(0);
    };
    public List<BigInteger> selectPolynomial() {
        SecureRandom random = new SecureRandom();
        List<BigInteger> coff = new ArrayList<>();
        for (int i = 0; i < threshold; i++) {
            coff.add(new BigInteger(256, random).mod(p));
        }
        return coff;
    }

    // Compute A_ik = g^a_ik mod p
    public  List<BigInteger> computeAValues() {
        List<BigInteger> A_values = new ArrayList<>();
        for (BigInteger coeff : coefficients) {
            A_values.add(modExp(generator, coeff, p));  // A_ik = g^a_ik mod p
        }
        return A_values;
    }

    // Compute the shares s_ij = f_i(j) mod p
    public  List<BigInteger> computeShares() {
        List<BigInteger> shares = new ArrayList<>();
        for (int j = 1; j <= n; j++) {
            BigInteger share = evaluatePolynomial(BigInteger.valueOf(j));
            shares.add(share);
        }
        return shares;
    }

    //f_i(j) mod p
    private BigInteger evaluatePolynomial(BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < coefficients.size(); i++) {
            result = result.add(coefficients.get(i).multiply(x.pow(i)));
        }
        return result.mod(p);
    }

    public static BigInteger modExp(BigInteger base, BigInteger exp, BigInteger mod) {
        return base.modPow(exp, mod);
    }

    public BigInteger CaculateSubKey(List<BigInteger> sValues) {
        BigInteger x_j = BigInteger.ZERO;
        for (BigInteger s_ij : sValues) {
            x_j = x_j.add(s_ij).mod(q);
        }
        sk = x_j;
        return x_j;
    }
    public BigInteger CaculateGPK(List<BigInteger> subPKShares) {
        BigInteger pk = BigInteger.ZERO;
        for(int i=0;i<subPKShares.size();i++)
        {
            pk.add(subPKShares.get(i)).mod(p);
        }
        return pk;
    }
    public BigInteger CaculateSubSignature(Message message) {
        String pk = String.valueOf(message.getBigInteger());
        return BLS.sign(sk,pk);
    }
}




