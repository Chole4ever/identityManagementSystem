package com.uav.node.demos.controller;

import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.contract.GDIDRegistry;
import com.uav.node.demos.model.GDDO;
import com.uav.node.demos.service.GDIDService;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.client.Client;
import org.fisco.bcos.sdk.v3.codec.ContractCodecException;
import org.fisco.bcos.sdk.v3.codec.datatypes.BytesType;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicArray;
import org.fisco.bcos.sdk.v3.codec.datatypes.DynamicBytes;
import org.fisco.bcos.sdk.v3.codec.datatypes.Utf8String;
import org.fisco.bcos.sdk.v3.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.v3.transaction.manager.AssembleTransactionProcessor;
import org.fisco.bcos.sdk.v3.transaction.manager.TransactionProcessorFactory;
import org.fisco.bcos.sdk.v3.transaction.model.dto.TransactionResponse;
import org.fisco.bcos.sdk.v3.transaction.model.exception.ContractException;
import org.fisco.bcos.sdk.v3.transaction.model.exception.TransactionBaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GroupController {
    private Client client;
    private CryptoKeyPair cryptoKeyPair;
    private AssembleTransactionProcessor transactionProcessor;


    public GroupController(FiscoBcos fiscoBcos) throws IOException {
        BcosSDK sdk = fiscoBcos.getBcosSDK();
        client = sdk.getClient("group0");
        cryptoKeyPair = client.getCryptoSuite().getCryptoKeyPair();
        transactionProcessor =
                TransactionProcessorFactory.createAssembleTransactionProcessor
                        (client, cryptoKeyPair,
                                "src/main/resources/abi/GDIDRegistry.abi",
                                "src/main/resources/bin/GDIDRegistry.bin");
    }
    @Value("${uav.gdidRegistryContractAddress}")
    private  String gdidRegistryContractAddress;

    @Autowired
    GDIDService gdidService;

    @PostMapping("//GDIDCreatioin")
    public ResponseEntity<Map<String, String>> updateDDO(@RequestBody GDDO gddo) throws IOException, TransactionBaseException, ContractCodecException {


//        Gto gto = GDIDService.getGto(gddo);
//
//        List<Object> params = new ArrayList<>();
//
//        params.add(gto.getAgg());
//        params.add(gddo.getLeaderdid());
//        params.add(gto.getPk());
//        params.add(gddo.getServiceList());
//        params.add(gddo.getDidList());
//
//        TransactionResponse transactionResponse =
//                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
//                        "GDIDRegistry",
//                        didRegistryContractAddress,
//                        "registerGDID",
//                        params);
        Map<String, String> response = new HashMap<>();
        response.put("Function","registerGDID");
        response.put("Status","success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deployGDIDRegistry")
    public ResponseEntity<Map<String, String>> deployGDIDRegistry() throws ContractException {


        GDIDRegistry gdidRegistry = GDIDRegistry.deploy(client, cryptoKeyPair);

        Map<String, String> response = new HashMap<>();
        response.put("Contract",gdidRegistry.getContractAddress());
        response.put("blockNumber", String.valueOf(client.getBlockNumber()));
        response.put("Message","deployGDIDRegistry");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/g2")
    public ResponseEntity<Map<String, String>> g2() throws Exception {

        String gdid_ ="did:group:6333285578434256823";
        String did_ ="did:UAV:979185578434256823";

        // 构造bytes[]参数
        String pk = "7142513692128887312957366097124764110363121271150334492810673144269002981787722838129250989787910354441465084994276682272592145092043385028452561113389658";
        byte[] pkList = pk.getBytes();
        DynamicBytes[] pkBytesArray = new DynamicBytes[]{ new DynamicBytes(pkList) };
        DynamicArray<DynamicBytes> pkListParam = new DynamicArray<>(DynamicBytes.class, pkBytesArray);

        // 构造serverList（string[]）
        List<Utf8String> serverArray = Arrays.asList(
                new Utf8String("rescue"),
                new Utf8String("transport")
        );
        DynamicArray<Utf8String> serverParam = new DynamicArray<>(Utf8String.class, serverArray.toArray(new Utf8String[0]));

        // 构造didLists（string[]）
        List<Utf8String> didArray = Arrays.asList(
                new Utf8String(did_),
                new Utf8String("did:UAV:6333285578434256823"),
                new Utf8String("did:UAV:6807135778434256823")
        );
        DynamicArray<Utf8String> didParam = new DynamicArray<>(Utf8String.class, didArray.toArray(new Utf8String[0]));

        // 构造参数列表
        List<Object> params = Arrays.asList(
                new Utf8String(gdid_),
                new Utf8String(did_),
                pkListParam,    // bytes[]
                serverParam,    // string[]
                didParam        // string[]
        );
        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "GDIDRegistry",
                        gdidRegistryContractAddress,
                        "registerGDID",
                        params);
        Map<String, String> response = new HashMap<>();
        response.put("Function","registerGDID");
        response.put("Status","success");
        response.put("transactionResponse",transactionResponse.getEvents());
        response.put("info",transactionResponse.toString());
        return ResponseEntity.ok(response);


    }

    @GetMapping("/g3")
    public ResponseEntity<Map<String, String>> g3() throws Exception {

        String gdid_ ="did:group:6333285578434256823";

        List<Object> params = new ArrayList<>();
        params.add(gdid_);


        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "GDIDRegistry",
                        gdidRegistryContractAddress,
                        "getGDIDDocument",
                        params);
        Map<String, String> response = new HashMap<>();
        response.put("Function","getGDIDDocument");
        response.put("Status","success");
        response.put("transactionResponse",transactionResponse.getEvents());
        response.put("info",transactionResponse.toString());
        return ResponseEntity.ok(response);


    }


}
