package com.light.rpc.service;

import com.light.rpc.export.HelloService;

/**
 * 服务的具体实现
 * 
 * @author chenlian
 *
 */
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String msg) {
        return "这是server端的响应" + msg;
    }

}
