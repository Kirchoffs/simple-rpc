package org.syh.prj.rpc.simplerpc.core.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.syh.prj.rpc.simplerpc.core.common.config.ClientConfig;
import org.syh.prj.rpc.simplerpc.core.common.config.PropertiesBootstrap;
import org.syh.prj.rpc.simplerpc.core.common.event.SimpleRpcListenerLoader;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcDecoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcEncoder;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcInvocation;
import org.syh.prj.rpc.simplerpc.core.common.protocol.RpcProtocol;
import org.syh.prj.rpc.simplerpc.core.common.utils.CommonUtils;
import org.syh.prj.rpc.simplerpc.core.proxy.jdk.JDKProxyFactory;
import org.syh.prj.rpc.simplerpc.core.registry.URL;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.AbstractRegister;
import org.syh.prj.rpc.simplerpc.core.registry.zookeeper.ZookeeperRegister;
import org.syh.prj.rpc.simplerpc.core.router.impl.DefaultRpcRouterImpl;
import org.syh.prj.rpc.simplerpc.core.router.impl.WeightedRpcRouterImpl;
import org.syh.prj.rpc.simplerpc.interfaces.DataService;
import org.syh.prj.rpc.simplerpc.core.proxy.javassit.JavassitProxyFactory;

import java.util.List;
import java.util.Map;

import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SEND_QUEUE;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.SUBSCRIBE_SERVICE_LIST;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.RPC_ROUTER;
import static org.syh.prj.rpc.simplerpc.core.common.cache.CommonClientCache.URL_MAP;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.JAVASSIT_PROXY;
import static org.syh.prj.rpc.simplerpc.core.common.constants.RpcConstants.RANDOM_ROUTER_TYPE;

public class Client {
    private final Logger logger = LogManager.getLogger(Client.class);
    public static EventLoopGroup clientGroup;
    private final Bootstrap bootstrap = new Bootstrap();
    private ClientConfig clientConfig;
    private AbstractRegister abstractRegister;
    private SimpleRpcListenerLoader simpleRpcListenerLoader;

    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public ClientConfig getClientConfig() {
        return clientConfig;
    }
    public void setClientConfig(ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
    }

    public RpcReference initClientApplication() throws InterruptedException {
        clientGroup = new NioEventLoopGroup();

        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcEncoder());
                ch.pipeline().addLast(new RpcDecoder());
                ch.pipeline().addLast(new ClientHandler());
            }
        });

        simpleRpcListenerLoader = new SimpleRpcListenerLoader();
        simpleRpcListenerLoader.init(); // The listener will update CONNECT_MAP

        clientConfig = PropertiesBootstrap.loadClientConfigFromLocal();

        RpcReference rpcReference;
        if (JAVASSIT_PROXY.equals(clientConfig.getProxyType())) {
            rpcReference = new RpcReference(new JavassitProxyFactory());
        } else {
            rpcReference = new RpcReference(new JDKProxyFactory());
        }

        return rpcReference;
    }

    private void initClientConfig() {
        String routerStrategy = clientConfig.getRouterStrategy();
        if (RANDOM_ROUTER_TYPE.equals(routerStrategy)) {
            RPC_ROUTER = new WeightedRpcRouterImpl();
        } else {
            RPC_ROUTER = new DefaultRpcRouterImpl();
        }
    }

    public void doSubscribeService(Class serviceBean) {
        if (abstractRegister == null) {
            abstractRegister = new ZookeeperRegister(clientConfig.getRegisterAddr());
        }

        URL url = new URL();
        url.setApplicationName(clientConfig.getApplicationName());
        url.setServiceName(serviceBean.getName());
        url.addParameter("host", CommonUtils.getIpAddress());

        Map<String, String> result = abstractRegister.getServiceDetailMap(serviceBean.getName());
        URL_MAP.put(serviceBean.getName(), result);

        abstractRegister.subscribe(url);
    }

    public void doConnectServer() {
        for (URL providerUrl: SUBSCRIBE_SERVICE_LIST) {
            List<String> providerIps = abstractRegister.getProviderIps(providerUrl.getServiceName());
            for (String providerIp: providerIps) {
                try {
                    ConnectionHandler.connect(providerUrl.getServiceName(), providerIp);
                } catch (InterruptedException e) {
                    logger.error("[doConnectServer] failed to connect to the provider ", e);
                }
            }
            URL url = new URL();
            url.addParameter("servicePath", providerUrl.getServiceName() + "/provider");
            url.addParameter("providerIps", String.join(",", providerIps));
            abstractRegister.doAfterSubscribe(url);
        }
    }

    private void startClient() {
        Thread asyncSendJob = new Thread(new AsyncSendJob());
        asyncSendJob.start();
    }

    class AsyncSendJob implements Runnable {
        private ObjectMapper mapper = new ObjectMapper();

        public AsyncSendJob() {}

        @Override
        public void run() {
            while (true) {
                try {
                    RpcInvocation data = SEND_QUEUE.take();
                    String json = mapper.writeValueAsString(data);
                    RpcProtocol rpcProtocol = new RpcProtocol(json.getBytes());
                    ChannelFuture channelFuture = ConnectionHandler.getChannelFuture(data.getTargetServiceName());
                    channelFuture.channel().writeAndFlush(rpcProtocol);
                } catch (InterruptedException | JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Client client = new Client();
        RpcReference rpcReference = client.initClientApplication();
        client.initClientConfig();
        DataService dataService = rpcReference.get(DataService.class);
        client.doSubscribeService(DataService.class);
        ConnectionHandler.setBootstrap(client.getBootstrap());
        client.doConnectServer();
        client.startClient();

        for (int i = 0; i < 100; i++) {
            try {
                String result = dataService.sendData("test");
                System.out.println(i + ": " + result);
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Done");
    }
}
