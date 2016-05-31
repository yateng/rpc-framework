package com.demo.rpc.startup;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProviderBoostrap {

    public static void main(String[] args) {

        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring-rpc-provider.xml");

        synchronized (ProviderBoostrap.class) {
            while (true) {
                try {
                    ProviderBoostrap.class.wait();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    appContext.close();
                }
            }
        }
    }
}
