package com.light.rpc.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

public class RegistryService implements Registry {

    private final Logger logger = LoggerFactory.getLogger(RegistryService.class);

    private ZookeeperClient zookeeper;

    public void setZookeeper(ZookeeperClient zookeeper) {
        this.zookeeper = zookeeper;
    }

    @Override
    public boolean registry(String group, String serviceName, String serviceAddress) {
        try {
            String groupPath = base_path + "/" + group;
            String servicePath = base_path + "/" + group + "/" + serviceName;
            if (!zookeeper.exite(base_path)) {
                zookeeper.create(base_path, null, true);
            }

            if (!zookeeper.exite(groupPath)) {
                zookeeper.create(groupPath, null, true);
            }

            if (!zookeeper.exite(servicePath)) {
                zookeeper.create(servicePath, null, true);
            }
            if (!zookeeper.exite(servicePath + "/" + serviceAddress)) {
                zookeeper.create(servicePath + "/" + serviceAddress+"-suffix", null, false); // 临时节点
            }
            return true;
        } catch (Exception e) {
            logger.error("服务地址注册到注册中心失败");
            throw new RuntimeException("服务地址注册到注册中心失败", e);
        }
    }

    @Override
    public Map<String, List<String>> subscript(String group) {
        Map<String, List<String>> serviceMap = new HashMap<String, List<String>>();
        // String servicePath = base_path + "/" + group + "/" + serviceName;
        try {
            String groupPath = base_path + "/" + group;
            List<String> groupList = zookeeper.getChildren(groupPath);
            for (String service : groupList) {
                String servicePath = groupPath + "/" + service;
                List<String> addressList = zookeeper.getChildren(servicePath);
                if (CollectionUtils.isEmpty(addressList)) {
                    throw new RuntimeException("找不到分组[" + group + "]发布的服务 " + service);
                }
                List<String> subAddress = new ArrayList<String>(addressList.size());
                for (String address : addressList) {
                    address = address.substring(0, address.lastIndexOf("-suffix"));
                    subAddress.add(address);
                }
                serviceMap.put(service, subAddress);
            }
            return serviceMap;
        } catch (Exception e) {
            logger.error("服务地址注册到注册中心失败");
            throw new RuntimeException("获取注册中心的服务地址失败!", e);
        }

    }

}
