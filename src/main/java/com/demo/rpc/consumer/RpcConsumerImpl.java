package com.demo.rpc.consumer;

import com.demo.rpc.export.HelloService;
import com.demo.rpc.framework.RpcFramework;

public class RpcConsumerImpl implements RpcConsumer {

    private RpcFramework framework;

    private String host;

    public String sayHello() {
        String result = "";
        try {
            HelloService service = framework.refer(HelloService.class, host);
            result = service.say("World");
        } catch (Exception e) {
            throw new RuntimeException("-----调用失败---------");
        }
        return result;
    }

    public void setFramework(RpcFramework framework) {
        this.framework = framework;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int hashCode() {
        return host.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RpcConsumerImpl rci = (RpcConsumerImpl) o;

        return host != rci.host ? false : true;
    }

}
