package com.demo.rpc.startup;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.demo.rpc.consumer.RpcConsumer;

public class ConsumerBootstrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring-rpc-consumer.xml");
        RpcConsumer consumer = (RpcConsumer) appContext.getBean("rfb");
        String result = consumer.sayHello();
        System.out.println(result);
    }
}
