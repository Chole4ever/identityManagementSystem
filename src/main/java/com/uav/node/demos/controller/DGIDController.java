package com.uav.node.demos.controller;


import com.uav.node.demos.config.FiscoBcos;
import com.uav.node.demos.network.UDPClient;
import com.uav.node.demos.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DGIDController {
    @Autowired
    FiscoBcos fiscoBcos;
    @Autowired
    TransportService transportService;
    @Autowired
    GDIDService gdidService;

    @Autowired
    UDPClient udpClient;

    //DIDG验证
    @GetMapping("/launchGDIDGeneration")
    public ResponseEntity<String> launchGDIDGeneration() throws Exception {
        gdidService.launchGDIDGeneration();
        return ResponseEntity.ok("hello world!");
    }

    //本次SK存储
    @GetMapping("/storeSK")
    public ResponseEntity<String> storeSK() throws Exception {
        gdidService.storeSK();
        return ResponseEntity.ok("hello world!");
    }





}
