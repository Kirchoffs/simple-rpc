# simple-rpc

## Notes
### Indicate the proxy
Use the VM option: `-Dproxy=javassist` or `-Dproxy=jdk`

### How does client get the object of service
```
DataService dataService = rpcReference.get(DataService.class);
```
It calls the method:
```
public <T> T get(Class<T> tClass) throws Throwable {
    return proxyFactory.getProxy(tClass);
}
```
If we choose JDK proxy, then it will call the method `getProxy` from `JDKProxyFactory` class to get the actual object by Java reflect:
```
public <T> T getProxy(final Class clazz) {

    return (T) Proxy.newProxyInstance(
        clazz.getClassLoader(),
        new Class[] { clazz },
        new JDKClientInvocationHandler(clazz)
    );
}
```
In `invoke` method of `InvocationHandler`, we can see how it works:
```
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    RpcInvocation rpcInvocation = new RpcInvocation();
    rpcInvocation.setArgs(args);
    rpcInvocation.setTargetMethod(method.getName());
    rpcInvocation.setTargetServiceName(clazz.getName());
    rpcInvocation.setUuid(UUID.randomUUID().toString());
    RESP_MAP.put(rpcInvocation.getUuid(), OBJECT);
    SEND_QUEUE.add(rpcInvocation);
    long beginTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - beginTime < 3 * 1000) {
        Object object = RESP_MAP.get(rpcInvocation.getUuid());
        if (object instanceof RpcInvocation) {
            return ((RpcInvocation) object).getResponse();
        }
    }
    throw new TimeoutException("response timeout!");
}
```
We keep checking the `RESP_MAP` to see if there are any new updates.
And in client handler we will update this map once we get the response from Netty server.