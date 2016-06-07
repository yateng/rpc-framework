package com.light.rpc.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumerConfig {

    private boolean useEpoll;

    private int port = 10086;

    private String group;
    
    private Map<String, List<String>> serviceAddressMap = new HashMap<String, List<String>>();

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, List<String>> getServiceAddressMap() {
        return serviceAddressMap;
    }
    
    public void setServiceAddressMap(Map<String, List<String>> serviceAddressMap) {
        this.serviceAddressMap = serviceAddressMap;
    }

}
