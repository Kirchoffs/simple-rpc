package org.syh.prj.rpc.simplerpc.jmh.common;

import java.io.Serializable;

public class User implements Serializable {
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
