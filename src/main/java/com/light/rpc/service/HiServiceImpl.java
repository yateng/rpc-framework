package com.light.rpc.service;

import com.light.rpc.export.HiService;

public class HiServiceImpl implements HiService {

    @Override
    public String hi(String msg) {
        return "Hi " + msg;
    }

}
