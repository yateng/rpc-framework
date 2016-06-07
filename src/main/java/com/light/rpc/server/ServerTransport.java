package com.light.rpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.light.rpc.common.Request;
import com.light.rpc.common.Response;
import com.light.rpc.registry.Registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author chenlian
 *
 */
public class ServerTransport implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Registry registry;

    private Map<String, Object> serviceMap;

    private ProviderConfig providerConfig;

    // 模仿tomcat的风格，把属性的getter和setter放到属性后边
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setServiceMap(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    public void setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
            .channel(providerConfig.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
    
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                            .addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader()))) // 请求对象的一个解码包装器
                            .addLast(new ObjectEncoder())
                            .addLast(new Handler());                     // 这儿会去调用实际的实现类，处理该请求
                        }
                    })
            .option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, false)
            .childOption(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 8 * 1024);
    
            // 绑定当前主机IP和端口
            ChannelFuture future = bootstrap.bind(providerConfig.getHost(), providerConfig.getPort()).sync();
            // 注册 RPC 服务地址
            for (String interfaceName : serviceMap.keySet()) {
                registry.registry(providerConfig.getGroup(), interfaceName, providerConfig.getHost());
                if (logger.isDebugEnabled()) {
                    logger.debug("注册服务:分组 {}-{} 在 {}:{}", providerConfig.getGroup(), interfaceName, providerConfig.getHost(), providerConfig.getPort());
                }
            }
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }


    class Handler extends SimpleChannelInboundHandler<Request> {

        @Override
        public void channelRead0(final ChannelHandlerContext ctx, Request request) throws Exception {
            Response rep = new Response();
            rep.setId(request.getId());
            try {
                rep.setResult(handle(request));// 这里会通过反射的方式真正调用服务
            } catch (Exception e) {
                rep.setException(e);
            }
            // 不管成功或者失败，都要写到响应信息里面,让客户端自己去捕获
            ctx.writeAndFlush(rep).addListener(ChannelFutureListener.CLOSE);
        }

        private Object handle(Request req) throws Exception {
            String serviceName = req.getInterfaceName();
            String group = req.getGroup();
            Object serviceBean = serviceMap.get(serviceName); // group + "-" + 
            if (serviceBean == null) {
                throw new RuntimeException(("找不到[ " + serviceName + " ]服务"));
            }

            Class<?> serviceClass = serviceBean.getClass();
            String methodName = req.getMethodName();
            Class<?>[] parameterTypes = req.getParameterTypes();
            Object[] parameters = req.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, parameters);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error("服务端异常,", cause);
            ctx.close();
        }
    }
}
