package com.cn.client.zookeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ShuffleTest {

    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        Collections.shuffle(list);

        System.out.println(list);
    }
}
