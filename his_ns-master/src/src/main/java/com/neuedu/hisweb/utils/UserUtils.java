package com.neuedu.hisweb.utils;

import com.neuedu.hisweb.entity.Customer;
import com.neuedu.hisweb.entity.User;

public class UserUtils {
    //线程变量，存放user实体类信息，即使是静态的与其他线程也是隔离的
    private static final ThreadLocal<User> userThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Customer> customerThreadLocal = new ThreadLocal<>();

    //获取当前登录用户
    public static User getLoginUser() {
        return userThreadLocal.get();
    }
    public static Customer getLoginCustomer() {
        return customerThreadLocal.get();
    }

    public static void setLoginUser(User user) {
        userThreadLocal.set(user);
    }
    public static void setLoginCustomer(Customer customer) {
        customerThreadLocal.set(customer);
    }

    public static void removeUser(){
        userThreadLocal.remove();
    }
    public static void removeCustomer(){
        customerThreadLocal.remove();
    }
}
