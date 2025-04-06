package com.uav.node.demos.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uav.node.demos.service.TransportService;
import org.apache.milagro.amcl.BLS381.BIG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistStore {
    Logger logger = LoggerFactory.getLogger(PersistStore.class);

    public byte[] loadFromFile(String filePath,String key) throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(filePath))) {
            Map<String, byte[]> data = new ObjectMapper().readValue(reader, new TypeReference<Map<String, byte[]>>() {});
            return data.get(key);
        }
    }

    public void wirteToFile(String dirPath, String name,byte[] bytes) throws IOException {
        Path storageDir = Paths.get( "keystore");

        Files.createDirectories(storageDir);
        Map<String,byte[]> data = new HashMap<>();
        data.put(name,bytes);
        String filename = dirPath + ".json";
        try (Writer writer = Files.newBufferedWriter(storageDir.resolve(filename))) {
            new ObjectMapper().writeValue(writer, data);
        }
        logger.info("{} persisted to: {}",name, storageDir.resolve(filename));
    }

    public void wirteToFile(String dirPath, String name,Map<String,byte[]> data) throws IOException {
        Path storageDir = Paths.get( "keystore");
        Files.createDirectories(storageDir);
        String filename = dirPath + ".json";
        try (Writer writer = Files.newBufferedWriter(storageDir.resolve(filename))) {
            new ObjectMapper().writeValue(writer, data);
        }
        logger.info("{} persisted to: {}",name, storageDir.resolve(filename));
    }


    public static void main(String[] args) throws IOException {
        PersistStore persistStore = new PersistStore();
        byte[] ans = persistStore.loadFromFile("./keystore/sk.json","sk");
        BIG bb = BIG.fromBytes(ans);
        System.out.println(bb);
        //0000000000000000000000000000000045e88a2828bc217dcbf8f7e4b5fa871b3d80e184cdf40ba4571883d6717630bf
        //0000000000000000000000000000000045e88a2828bc217dcbf8f7e4b5fa871b3d80e184cdf40ba4571883d6717630bf


    }
}
