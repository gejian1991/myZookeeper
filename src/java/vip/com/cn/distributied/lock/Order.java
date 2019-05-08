package com.cn.distributied.lock;

public class Order {
    public void createOrder(){
        System.out.println(Thread.currentThread().getName()+":"+"创建订单");
    }
}
