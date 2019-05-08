package com.cn.client.zkClient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

/**
 * 用来修改数据
 */
public class ZkClientWatchTest {

    public static void main(String[] args) {
        ZkClient zk = new ZkClient("localhost:2181", 10000, 10000, new SerializableSerializer());

        zk.writeData("/data", "7");
    }
}
