package org.syh.prj.rpc.simplerpc.core.serialize;

import org.junit.jupiter.api.Test;
import org.syh.prj.rpc.simplerpc.core.serialize.hessian.HessianSerializeFactory;
import org.syh.prj.rpc.simplerpc.core.serialize.jackson.JacksonSerializeFactory;
import org.syh.prj.rpc.simplerpc.core.serialize.jdk.JdkSerializeFactory;
import org.syh.prj.rpc.simplerpc.core.serialize.kryo.KryoSerializeFactory;

import java.io.Serializable;

public class SerializeByteSizeCompareTest {

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

    public void jdkSerializeSizeTest() {
        SerializeFactory serializeFactory = new JdkSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result, User.class);
        System.out.println("JDK's size is " + result.length);
    }

    public void hessianSerializeSizeTest() {
        SerializeFactory serializeFactory = new HessianSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result, User.class);
        System.out.println("Hessian's size is " + result.length);
    }

    public void jacksonSerializeSizeTest() {
        SerializeFactory serializeFactory = new JacksonSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result, User.class);
        System.out.println("Jackson's size is " + result.length);
    }

    public void kryoSerializeSizeTest() {
        SerializeFactory serializeFactory = new KryoSerializeFactory();
        User user = buildUserDefault();
        byte[] result = serializeFactory.serialize(user);
        User deserializeUser = serializeFactory.deserialize(result, User.class);
        System.out.println("Kryo's size is " + result.length);
    }

    @Test
    public void compare() {
        jacksonSerializeSizeTest();
        jdkSerializeSizeTest();
        kryoSerializeSizeTest();
        hessianSerializeSizeTest();
    }
}

class User implements Serializable {
    private static final long serialVersionUID = 5342726393385586669L;

    private Integer id;

    private String username;

    private String cardNumber;

    private String phoneNumber;

    private Integer age;

    private Integer gender;

    private Long accountNumber;

    private String address;

    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return
                "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", accountNumber=" + accountNumber +
                ", address='" + address + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
