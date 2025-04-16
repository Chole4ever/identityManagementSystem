package com.uav.node.demos.controller;

import com.uav.node.demos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BGASController {

    @Autowired
    AuthService authService;

    //bgas验证
    @GetMapping("/authGroup")
    public String authGroup() throws Exception {
        authService.authGroup();
        return "hello";
    }

    @GetMapping("/storeGroupVC")
    public String storeGroupVC() throws Exception {

        return "hello";
    }

}
