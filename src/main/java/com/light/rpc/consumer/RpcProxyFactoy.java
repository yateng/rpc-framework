package com.light.rpc.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.light.rpc.common.Request;
import com.light.rpc.common.Response;

public class RpcProxyFactoy {

    private final static Logger logger = LoggerFactory.getLogger(RpcProxyFactoy.class);

    @SuppressWarnings("unchecked")
    public static <T> T getRpcServerProxy(final ConsumerConfig config, String className) throws Exception {
        final Map<String, List<String>> map = config.getServiceAddressMap();
        // Class<?> z = Class.forName(className);
        Class<?>[] interfaces = { Class.forName(className) };
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), interfaces, new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Request req = new Request();
                req.setId(UUID.randomUUID().toString());
                req.setGroup(config.getGroup());
                req.setInterfaceName(method.getDeclaringClass().getName());
                req.setMethodName(method.getName());
                req.setParameterTypes(method.getParameterTypes());
                req.setParameters(args);

                List<String> addressList = map.get(method.getDeclaringClass().getName());
                String host = addressList.get(new Random().nextInt(addressList.size()));

                ConsumerInvoke client = new ConsumerInvoke(host, 10086);
                long time = System.currentTimeMillis();
                Response resp = client.invoke(req);
                logger.debug(req.getInterfaceName() + "-->" + req.getMethodName() + " 执行时间:{}", (System.currentTimeMillis() - time));
                if (resp == null) {
                    throw new RuntimeException("response is null");
                }
                // 响应结果
                if (resp.hasException()) {
                    throw resp.getException();
                } else {
                    return resp.getResult();
                }
            }
        });
    }

}
