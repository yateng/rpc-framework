package com.light.rpc.startup;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.light.rpc.export.HelloService;
import com.light.rpc.export.HiService;

public class ClientBootstrap {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring-rpc-consumer.xml");

        for (int i = 0; i < 1000; i++) {
            HelloService helloService = (HelloService) appContext.getBean("helloServiceRpc");
            System.out.println(helloService.hello("client say hello" + i));

            HiService hiService = (HiService) appContext.getBean("hiServiceRpc");
            System.out.println(hiService.hi("client say hi" + i));

            System.out.println("-------------------------------------");
        }
    }
}
