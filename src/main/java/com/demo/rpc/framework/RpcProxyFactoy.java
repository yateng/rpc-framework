package com.demo.rpc.framework;

import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.Map;

public class RpcProxyFactoy {

    @SuppressWarnings("unchecked")
    public static <T> T getRpcServerProxy(Map<Integer, T> serversMap) {
        RpcConsumerHandler<T> serverHander = new RpcConsumerHandler<T>(serversMap);
        T proxy = null;
        for (Iterator<T> iterator = serversMap.values().iterator(); iterator.hasNext();) {
            T server = iterator.next();
            proxy = (T) Proxy.newProxyInstance(server.getClass().getClassLoader(), server.getClass().getInterfaces(), serverHander);
            break;
        }
        return proxy;
    }
}
