package com.uav.node.demos.controller;



import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.contract.DIDRegistry;
import com.uav.node.demos.contract.GDIDRegistry;
import com.uav.node.demos.crypto.Secp256k;
import com.uav.node.demos.model.DDO;
import com.uav.node.demos.model.Node;
import com.uav.node.demos.service.GDIDService;
import lombok.Getter;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.ECKeyPair;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ClientController {
    private BcosSDK sdk;
    private Client client;
    private CryptoKeyPair cryptoKeyPair;

    private AssembleTransactionProcessor transactionProcessor;
    public ClientController(FiscoBcos fiscoBcos) throws IOException {
        sdk =  fiscoBcos.getBcosSDK();
        client = sdk.getClient("group0");
        cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
         transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/DIDRegistry.abi",
                                "src/main/resources/bin/DIDRegistry.bin");
    }
    @Value("${uav.didRegistryContractAddress}")
    private  String didRegistryContractAddress;

    @Autowired
    GDIDService gdidService;

    @GetMapping("/GenerateECCkeyPair")
    public ResponseEntity<Map<String, String>> generateKeyPair() {
        ECKeyPair eccKeyPair = Secp256k.generateKeyPair();
        Map<String, String> response = new HashMap<>();
        response.put("PubKey", eccKeyPair.getPublicKey().toString());
        response.put("SecretKey", eccKeyPair.getPrivateKey().toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/GenerateDID")
    public ResponseEntity<Map<String, String>> generateDID(
            @RequestParam("username") String username
    ) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(username.getBytes());

        long hashValue = 0;
        for (int i = 0; i < 8; i++) {
            hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
        }
        if (hashValue < 0) {
            hashValue = -hashValue;
        }
        String did = "did:UAV:"+hashValue;
        Map<String, String> response = new HashMap<>();
        response.put("DID",did);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/findDID")
    public ResponseEntity<Map<String, String>> findDID( @RequestParam("did") String did) throws IOException, TransactionBaseException, ContractCodecException {

        // 创建调用交易函数的参数
        //string memory did, string[] memory publicKeys, string[] memory serviceList
        List<Object> params = new ArrayList<>();
        params.add(did);

        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "DIDRegistry",
                        didRegistryContractAddress,
                        "getDIDDocument",
                        params);

        List<Object> list =  transactionResponse.getReturnObject();

        // 假设你有一个结构化的 Map 或自定义类
        Map<String, String > response = new HashMap<>();

        response.put("Function", "findDID");
        response.put("DID", (String) list.get(0));
        response.put("GDID", (String) list.get(1));
        response.put("PublicKeys", String.valueOf(list.get(2)));
        response.put("ServerLists", String.valueOf(list.get(3)));
        response.put("Created", "1739887691");
        response.put("Updated", "1739978293");
//        dto dto = new dto("findDID",list);
//        response.put("function","findDID");
//        response.put("info",dto.toString());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/registerDID")
    public ResponseEntity<Map<String, String>> registerDID(@RequestBody Node node) throws IOException, TransactionBaseException, ContractCodecException {

        List<Object> params = new ArrayList<>();
        params.add(node.getDid());
        params.add(node.getPublicKeys());
        params.add(node.getServiceList());
        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "DIDRegistry",
                        didRegistryContractAddress,
                        "registerDID",
                        params);
        Map<String, String> response = new HashMap<>();
        response.put("Function","registerDID");
        response.put("Status","success");
        response.put("transactionResponse",transactionResponse.getEvents());
        response.put("info",transactionResponse.toString());
        return ResponseEntity.ok(response);
    }


    @PostMapping("/groupAuthentication")
    public ResponseEntity<String> groupAuthentication(@RequestBody Node node) {
        // 逻辑处理：群组认证
        return ResponseEntity.ok("群组认证成功");
    }

    @PostMapping("/updateDDO")
    public ResponseEntity<Map<String, String>> updateDDO(@RequestBody DDO ddo) throws IOException, TransactionBaseException, ContractCodecException {

        //string memory did, string[] memory publicKeys, string[] memory serviceList
        List<Object> params = new ArrayList<>();
        //string memory did, string memory gdid_,string[] memory publicKeys_,string[] memory serviceList_) public {
        //
        params.add(ddo.getDid());
        if(ddo.getGdid()!=null)
            params.add(ddo.getGdid());
        params.add(ddo.getPublicKeys());
        params.add(ddo.getServiceList());

        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "DIDRegistry",
                        didRegistryContractAddress,
                        "updateDIDDocument",
                        params);
        Map<String, String> response = new HashMap<>();
        response.put("Function","updateDDO");
        response.put("Status","success");
        response.put("transactionResponse",transactionResponse.getEvents());
        response.put("info",transactionResponse.toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("batchRegisterDID")
    public ResponseEntity<Map<String, Map<String, String>> > batchRegisterDID() throws NoSuchAlgorithmException, TransactionBaseException, ContractCodecException {

        int num = 4;
        Map<String, Map<String, String>> response = new HashMap<>();

        for(int i=7;i<num+7;i++)
        {
            Map<String, String> uav = new HashMap<>();
            ECKeyPair eccKeyPair = Secp256k.generateKeyPair();
            String did = generateRandomDID("lalala"+i);

            List<Object> params = new ArrayList<>();
            params.add(did);
            List<String> pkl = new ArrayList<>();
            pkl.add(String.valueOf(eccKeyPair.getPublicKey()));
            params.add(pkl);
            List<String> serverl = new ArrayList<>();
            serverl.add("Delivery");
            serverl.add("Agricultural Monitoring");
            params.add(serverl);
            TransactionResponse transactionResponse =
                    transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                            "DIDRegistry",
                            didRegistryContractAddress,
                            "registerDID",
                            params);

            uav.put("did", did);
            uav.put("pk", String.valueOf(eccKeyPair.getPublicKey()));
            uav.put("sk", String.valueOf(eccKeyPair.getPrivateKey()));
            uav.put("response", transactionResponse.getEvents());
            response.put("uav"+i, uav);
        }

        return ResponseEntity.ok(response);
    }
    public String generateRandomDID(String username
    ) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(username.getBytes());

        long hashValue = 0;
        for (int i = 0; i < 8; i++) {
            hashValue = (hashValue << 8) | (hashBytes[i] & 0xFF);
        }
        if (hashValue < 0) {
            hashValue = -hashValue;
        }

        return "did:UAV:"+hashValue;
    }


}
