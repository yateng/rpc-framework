package com.demo.rpc.framework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author chenlian
 *
 * @param <T>
 */
public class RpcConsumerHandler<T> implements InvocationHandler {

    private final Map<Integer, T> serversMap;

    private Map<Object, AtomicInteger> allExcuteMap = new ConcurrentHashMap<Object, AtomicInteger>();       // 总共执行的次数

    private Map<Object, AtomicInteger> successExcuteMap = new ConcurrentHashMap<Object, AtomicInteger>();   // 成功执行的次数

    private Map<Object, Long> timeMap = new ConcurrentHashMap<Object, Long>();

    private static final float DEFAULT_SHIFT_FACTOR = 0.75f;

    public RpcConsumerHandler(Map<Integer, T> serversMap) {
        this.serversMap = serversMap;
    }

    /* 
     * a.自动容灾切换到下一个分组；
     * b.如果某一个分组在1分钟内成功率在75%以下，就不调用该分组，调用下个分组;
     * 
     * 
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if (serversMap == null || serversMap.size() < 1) {
            throw new RuntimeException("服务未配置，请配置服务");
        }
        List<T> serverList = new ArrayList<T>();
        int defaultService = 1;                                                 // 这个配置号可以通过外部zk或者dbconfig获取，读取默认分组号
        serverList.add(serversMap.get(defaultService));                         // 一般来说配置先走默认分组
        Iterator<Map.Entry<Integer, T>> it = serversMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, T> entry = it.next();
            if (!serverList.contains(entry.getValue())) {
                serverList.add(entry.getValue());
            }
        }
        for (int i = 0; i < serverList.size(); i++) {
            Object obj = serverList.get(i);
            try {
                
                if (allExcuteMap.get(obj) == null) {
                    allExcuteMap.put(obj, new AtomicInteger(1));
                } else {
                    allExcuteMap.get(obj).getAndIncrement();
                }
                if (timeMap.get(obj) == null) {
                    timeMap.put(obj, System.currentTimeMillis());
                }
                
                if (shift(obj)) {
                    timeMap.remove(obj);
                    allExcuteMap.remove(obj);
                    successExcuteMap.remove(obj);
                    continue;
                }
                
                result = method.invoke(obj, args);
                if (successExcuteMap.get(obj) == null) {
                    successExcuteMap.put(obj, new AtomicInteger(1));
                } else {
                    successExcuteMap.get(obj).getAndIncrement();
                }
                break;
            } catch (Exception e) {
                if (i == serverList.size() - 1) {
                    System.out.println("服务调用异常，无可用服务");
                    throw new RuntimeException("服务调用异常，无可用服务", e);
                } else {
                    System.out.println("服务调用异常自动切换到下一个备用服务");
                }
            }
        }
        return result;
    }

    private boolean shift(Object obj) {
        
        if(successExcuteMap.get(obj)==null) return false;
        
        float successCount = successExcuteMap.get(obj).get();
        float totalCount = allExcuteMap.get(obj).get();

        return (System.currentTimeMillis() - timeMap.get(obj) > 1000 * 60 && successCount / totalCount < DEFAULT_SHIFT_FACTOR) ? true : false;
    }

}
