# simple-rpc

## Project Notes
1\. RpcReference  

It is used to get the client stub object using dynamic proxy.  
It can be implemented by normal JDK dynamic proxy, or by Javassit.

2\. How does client connect to the servers or registry centers?  

• Connect to registry center:

In `Client.doSubscribeService`:
```
if (abstractRegister == null) {
    abstractRegister = new ZookeeperRegister(clientConfig.getRegisterAddr());
}
```

• Connect to servers:  

In `Client.java`:
```
// In main method
ConnectionHandler.setBootstrap(client.getBootstrap());

// In doConnectServer method
// Get IPs from zookeeper registry center, and go through IPs to make the connection.
for (URL providerUrl: SUBSCRIBE_SERVICE_LIST) {
    List<String> providerIps = abstractRegister.getProviderIps(providerUrl.getServiceName());
    fpr (String providerIp: providerIps) {
        ConnectionHandler.connect(providerServiceName, providerIp); 
    }
}
```

In `ConnectionHandler.java`:
```
ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
```

3\. Basic process from client side  

First call `initClientApplication` to setup Netty related stuff, load the client configuration, get the proxy manager for remote object.  
Then in `doSubscribeService` method, connect Zookeeper, and subscribe the service.  
After it, in `doConnectServer`, we go through the subscribe service list, and connect each one, get the corresponding `ChannelFuture`, and store
them in the `CONNECT_MAP`.  
Finally, we start a thread, check the `SEND_QUEUE`, if there are data in it, we will send it to the corresponding remote service.

4\. Basic process from server side  

First load the configuration, then put the service in `PROVIDER_URL_SET`. Finally set up the Netty stuff,
go through the `PROVIDER_URL_SET` and register with the Zookeeper. 


5\. How does router work?  
In `Client.java`, we have an async job kept running to send the request to the corresponding service.
```
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
```
Here we have `ConnectionHandler.getChannelFuture()` method, inside it:
```
ChannelFuture channelFuture = RPC_ROUTER.select(selector).getChannelFuture();
```


## Netty Notes
### ChannelFuture
In Netty, a ChannelFuture represents the result of an asynchronous I/O operation. It is a Java Future object that can be used to track the progress of an operation and obtain the result when it is complete.

ChannelFuture objects are returned by various Netty APIs when an I/O operation is initiated asynchronously. For example, when you send a message over a Channel using write() method, the method returns a ChannelFuture object representing the result of the write operation. You can use this ChannelFuture to track the progress of the write operation and get notified when it is complete.

Example:
```
Channel ch = ...;
ByteBuf data = ...;

// Send the data over the channel and get a ChannelFuture representing the result
ChannelFuture future = ch.write(data);

// Add a listener to be notified when the write operation is complete
future.addListener(new ChannelFutureListener() {
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess()) {
            System.out.println("Write successful");
        } else {
            System.out.println("Write failed");
            future.cause().printStackTrace();
        }
    }
});
```

### Bootstrap
```
// Create a new Bootstrap instance
Bootstrap bootstrap = new Bootstrap();

// Set up the event loop group for the connection
EventLoopGroup group = new NioEventLoopGroup();
bootstrap.group(group);

// Set the channel type and configure the pipeline
bootstrap.channel(NioSocketChannel.class)
         .handler(new CustomizedChannelInitializer());

// Connect to the remote server
ChannelFuture future = bootstrap.connect("localhost", 8080);

// Wait for the connection to be established
future.sync();
```

## Others
### Load Resources
Use `XxxClass.class.getClassLoader().getResourceAsStream("path")` method
```
Properties properties = new Properties();
properties.load(PropertiesLoader.class.getClassLoader().getResourceAsStream(DEFAULT_PROPERTIES_FILE));
```
```
ObjectMapper objectMapper = new ObjectMapper();
Student student = objectMapper.readValue(
    ObjectLoader.class.getClassLoader().getResourceAsStream(DEFAULT_OBJECT_FILE),
    Student.class
);
```
`getResourceAsStream("path")` will look for the file under the directory __resources__ in Maven project.  

However, if we use `new File(path)`, the pathname can be either an absolute pathname (a full path that starts with the root directory of the file system) 
or a relative pathname (a path that is relative to the current working directory).  
The File class does not use the classpath to search for files. Instead, it uses the file system to search for files. 
If you pass an absolute pathname to the File constructor, it will look for the file at the specified location on the file system. 
If you pass a relative pathname, it will look for the file relative to the current working directory.
