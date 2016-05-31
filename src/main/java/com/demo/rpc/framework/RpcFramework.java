package com.demo.rpc.framework;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;

public class RpcFramework {

    private int port = 10086;   // 先给个默认的

    public void export(final Object service) throws Exception {
        if (service == null) {
            throw new IllegalArgumentException("服务不能为空！");
        }

        System.out.println("发布服务 [" + service.getClass().getName() + "] 端口[ " + port + " ]");
        ServerSocket server = new ServerSocket(port);
        while (true) {
            try {
                final Socket socket = server.accept();
                new Thread(new Runnable() {

                    public void run() {
                        try {
                            try {
                                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                                try {
                                    String methodName = ois.readUTF();
                                    Class<?>[] parameterTypes = (Class<?>[]) ois.readObject();
                                    Object[] arguments = (Object[]) ois.readObject();
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                    try {
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                                        Object result = method.invoke(service, arguments);
                                        output.writeObject(result);
                                    } catch (Throwable t) {
                                        output.writeObject(t);
                                    } finally {
                                        output.close();
                                    }
                                } finally {
                                    ois.close();
                                }
                            } finally {
                                socket.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T refer(final Class<T> interfaceClass, final String host) throws Exception {
        if (interfaceClass == null || !interfaceClass.isInterface()) {
            throw new IllegalArgumentException("接口不能为空" + (null == interfaceClass ? "" : " ,或" + interfaceClass.getName() + "必须是接口") + "");
        }
        System.out.println("获取服务信息 " + interfaceClass.getName() + " [server " + host + ":" + port + "]");
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass }, new InvocationHandler() {

            public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
                Socket socket = new Socket(host, port);         // 其实这儿有个小bug，当host的机器是挂掉的情况，下面这个超时是无效的
                socket.setSoTimeout(1000);
                try {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    try {
                        output.writeUTF(method.getName());
                        output.writeObject(method.getParameterTypes());
                        output.writeObject(arguments);
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                        try {
                            Object result = input.readObject();
                            if (result instanceof Throwable) {
                                throw (Throwable) result;
                            }
                            System.out.println("获取服务信息 " + interfaceClass.getName() + "成功！ [server " + host + ":" + port + "]");
                            return result;
                        } finally {
                            input.close();
                        }
                    } finally {
                        output.close();
                    }
                } finally {
                    socket.close();
                }
            }
        });
    }

    public void setPort(int port) {
        this.port = port;
    }
    

}
