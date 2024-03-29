package org.syh.prj.rpc.simplerpc.core.common.constants;

public class RpcConstants {
    public static final short MAGIC_NUMBER = 2718;
    public static final String JAVASSIT_PROXY = "javassist";
    public static final String JDK_PROXY = "jdk";
    public static final String RANDOM_ROUTER_TYPE = "random";
    public static final String JDK_SERIALIZE_TYPE = "jdk";
    public static final String JACKSON_SERIALIZE_TYPE = "jackson";
    public static final String HESSIAN_SERIALIZE_TYPE = "hessian";
    public static final String KRYO_SERIALIZE_TYPE = "kryo";
    public static final Integer DEFAULT_QUEUE_SIZE = 512;
    public static final Integer DEFAULT_THREAD_NUMS = 256;
    public static final String DEFAULT_DELIMITER = "viburnum";
    public static final Integer DEFAULT_MAX_CONNECTIONS = DEFAULT_QUEUE_SIZE + DEFAULT_THREAD_NUMS;
    public static final Integer SERVER_DEFAULT_MSG_LENGTH = 1024 * 10;
    public static final Integer CLIENT_DEFAULT_MSG_LENGTH = 1024 * 10;
    public static final Integer DEFAULT_TIMEOUT = 5000;
}
