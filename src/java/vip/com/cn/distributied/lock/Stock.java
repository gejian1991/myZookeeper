package com.cn.distributied.lock;

public class Stock {
    private static Integer COUNT=1;

    public boolean reduceCount(){
        if(COUNT>0){
            COUNT--;
            return true;
        }
        return false;
    }
}
