package com.demo.rpc.service;

import com.demo.rpc.export.HelloService;

public class HelloServiceImpl implements HelloService {

    public String say(String words) {
        return "Hi! " + words;
    }

}
