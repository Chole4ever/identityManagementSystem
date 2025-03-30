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
import org.springframework.web.bind.annotation.*;

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

    //did:group:1739218670843064809

    @PostMapping("/findGDID")
    public ResponseEntity<Map<String, String>> findGDID( @RequestParam("did") String did) throws IOException, TransactionBaseException, ContractCodecException {

        // 创建调用交易函数的参数
        //string memory did, string[] memory publicKeys, string[] memory serviceList
        List<Object> params = new ArrayList<>();
        params.add(did);

        // 调用HelloWorld合约，合约地址为helloWorldAddress， 调用函数名为『set』，函数参数类型为params
        TransactionResponse transactionResponse =
                transactionProcessor.sendTransactionAndGetResponseByContractLoader(
                        "GDIDRegistry",
                        gdidRegistryContractAddress,
                        "getGDIDDocument",
                        params);

        List<Object> list =  transactionResponse.getReturnObject();

        // 假设你有一个结构化的 Map 或自定义类
        Map<String, String > response = new HashMap<>();

        response.put("Function", "getGDIDDocument");
        response.put("GDID", (String) list.get(0));
        response.put("PublicKeys", String.valueOf(list.get(2)));
        response.put("ServerLists", String.valueOf(list.get(3)));
        response.put("DidLists", String.valueOf(list.get(4)));
        response.put("seq", String.valueOf(list.get(5)));
        response.put("created", String.valueOf(list.get(6)));
        response.put("updated", String.valueOf(list.get(7)));

        return ResponseEntity.ok(response);
    }



}
