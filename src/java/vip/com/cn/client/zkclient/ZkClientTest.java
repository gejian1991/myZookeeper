package com.cn.client.zkClient;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

import java.io.IOException;


public class ZkClientTest {

    public static void main(String[] args) throws IOException {
        ZkClient zk = new ZkClient("localhost:2181", 10000,
                10000, new SerializableSerializer());

        //创建临时节点
//        zk.createPersistent("/data", "1".getBytes());

        //订阅数据改变
        zk.subscribeDataChanges("/data", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("数据被改了");
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                System.out.println("数据删除");

            }
        });

        System.in.read();

    }
}
