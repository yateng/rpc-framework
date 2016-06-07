package com.light.rpc.consumer;

import org.springframework.beans.factory.InitializingBean;

import com.light.rpc.registry.Registry;

public class ConsumerTransport implements InitializingBean {

    private Registry registry;

    private ConsumerConfig consumerConfig;

    // 模仿tomcat的风格，把属性的getter和setter放到属性后边
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setConsumerConfig(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        consumerConfig.setServiceAddressMap(registry.subscript(consumerConfig.getGroup()));
    }

}
