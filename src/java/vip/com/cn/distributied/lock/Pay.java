package com.cn.distributied.lock;

public class Pay {
    public void pay(){
        System.out.println(Thread.currentThread().getName()+":"+"支付成功");
    }
}
