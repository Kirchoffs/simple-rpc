package org.syh.prj.rpc.simplerpc.jmh.serialize;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.syh.prj.rpc.simplerpc.core.serialize.SerializeFactory;
import org.syh.prj.rpc.simplerpc.core.serialize.hessian.HessianSerializeFactory;
import org.syh.prj.rpc.simplerpc.core.serialize.jackson.JacksonSerializeFactory;
import org.syh.prj.rpc.simplerpc.core.serialize.jdk.JdkSerializeFactory;
import org.syh.prj.rpc.simplerpc.core.serialize.kryo.KryoSerializeFactory;
import org.syh.prj.rpc.simplerpc.jmh.common.User;

public class SerializeCompareTest {
    private static User buildUserDefault() {
        User user = new User();
        user.setAge(28);
        user.setAddress("1223 w 25th street");
        user.setAccountNumber(2147483548L);
        user.setGender(1);
        user.setId(2718);
        user.setCardNumber("4294967296");
        user.setRemark("null");
        user.setUsername("akira");
        return user;
    }

    @Benchmark
    public void jdkSerializeTest(){
        SerializeFactory serializeFactory = new JdkSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result,User.class);
    }

    @Benchmark
    public void hessianSerializeTest(){
        SerializeFactory serializeFactory = new HessianSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result,User.class);
    }

    @Benchmark
    public void jacksonSerializeTest(){
        SerializeFactory serializeFactory = new JacksonSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result,User.class);
    }

    @Benchmark
    public void kryoSerializeTest(){
        SerializeFactory serializeFactory = new KryoSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result,User.class);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .warmupIterations(2)
                .measurementBatchSize(2)
                .forks(1)
                .build();

        new Runner(options).run();
    }
}
