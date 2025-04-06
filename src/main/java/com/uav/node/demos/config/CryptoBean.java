package com.uav.node.demos.config;

import com.uav.node.demos.util.PersistStore;
import lombok.Data;
import org.apache.milagro.amcl.BLS381.BIG;
import org.apache.milagro.amcl.BLS381.ECP;
import org.apache.milagro.amcl.BLS381.ECP2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.web3j.crypto.ECKeyPair;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ECP2 pk_i;
    private ECP2 groupPubKey;
    private HashMap<Integer,ECP> partialSigs;
    private HashMap<Integer,ECP> partialSigsForGVP;
    private byte[] metadata;

    private ECKeyPair ecKeyPair;

    @PostConstruct
    public void init() {
        threshold = config.getThreshold();
        n = config.count;
        skshares = new ArrayList<>();
        pkshares = new ArrayList<>();
        partialSigs = new HashMap<>();

        BigInteger pri ;
        BigInteger pub ;

   //     ecKeyPair= new ECKeyPair(pri,pub);

    }

    public static void main(String[] args) throws IOException {
        String pubS = "11750041889058128693051513405714642470597351109013443500771875176821015924932586128540078877063798929256995968414023884206464513326415636107848565447946557";
        String pris = "67819058261383344764587448613810594878299821823224897200233789779397152707591";


        PersistStore persistStore = new PersistStore();
        Map<String,byte[]> data = new HashMap<>();

        BigInteger pri = new BigInteger(pris);
        BigInteger pub = new BigInteger(pubS);

        data.put("pri",pri.toByteArray());
        data.put("pub",pub.toByteArray());

        persistStore.wirteToFile("ecKeyPair","ecKeyPair",data);
        byte[] bytes = persistStore.loadFromFile("keystore/ecKeyPair.json","pri");
        byte[] bytes2 = persistStore.loadFromFile("keystore/ecKeyPair.json","pub");

        System.out.println(new BigInteger(bytes));

        System.out.println(new BigInteger(bytes2));

    }
}
