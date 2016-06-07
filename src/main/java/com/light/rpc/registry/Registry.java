package com.light.rpc.registry;

import java.util.List;
import java.util.Map;

/**
 * 注册中心，提供服务的注册和客户端的订阅
 * 
 * @author chenlian
 *
 */
public interface Registry {

    final String base_path = "/registry";

    /**
     * |registry
     * |
     * |--------group
     * |
     * |--------------service(接口名称)
     * |
     * |------------------------------服务地址
     * 
     * @param group
     * @param serviceName
     * @param address
     * @return
     */
    boolean registry(String group, String serviceName, String address);

    Map<String, List<String>> subscript(String group);

}
