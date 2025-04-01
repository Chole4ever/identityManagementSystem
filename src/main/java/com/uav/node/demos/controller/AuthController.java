package com.uav.node.demos.controller;

import com.uav.node.demos.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/authGroup")
    public String findGDID(@RequestParam("ip") String ip, @RequestParam("port") int port) throws Exception {
        authService.authGroup(ip, port);
        return "hello";
    }
}
