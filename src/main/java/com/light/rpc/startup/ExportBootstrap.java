package com.light.rpc.startup;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 发布服务启动入口
 * 
 * @author chenlian
 *
 */
public class ExportBootstrap {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("spring-rpc-provider.xml");
        synchronized (ExportBootstrap.class) {
            while (true) {
                try {
                    ExportBootstrap.class.wait();
                } catch (InterruptedException e) {
                    appContext.close();
                }
            }
        }
    }

}
