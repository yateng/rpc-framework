package com.demo.rpc.provider;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.demo.rpc.framework.RpcFramework;

/**
 * 发布服务
 * @author chenlian
 *
 * @param <T>
 */
public class RpcProvider<T> {

    private RpcFramework framework;

    private List<T> services;

    public void export() {

        if (!CollectionUtils.isEmpty(services)) {
            for (T t : services) {
                try {
                    framework.export(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setFramework(RpcFramework framework) {
        this.framework = framework;
    }

    public void setServices(List<T> services) {
        this.services = services;
    }
}
