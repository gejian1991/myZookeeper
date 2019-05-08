package com.cn.distributied.config;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();

        config.save("timeOut","500");
        for (int i=1;i<=100;i++){
            System.out.println(config.get("timeOut"));
            try{
                TimeUnit.SECONDS.sleep(5);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
