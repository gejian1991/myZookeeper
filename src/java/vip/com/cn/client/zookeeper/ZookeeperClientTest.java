package com.cn.client.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class ZookeeperClientTest {

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        // 默认的watch，监听器
        ZooKeeper client = new ZooKeeper("localhost:2181,localhost:2182", 5000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("连接的时候" + event);
            }
        });

        //获取数据
        Stat stat = new Stat();
        //1.监听器类型为Watcher
        String s=new String(client.getData("/testW", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                //监听器，监听节点数据改变
                if (Event.EventType.NodeDataChanged.equals(event.getType())) {
                    System.out.println("数据发送了改变");
                }
            }
        }, stat));
        System.out.println(s);

        //2.监听器类型为boolean，为true使用默认的监听器，client中定义的
        //String s2 = new String(client.getData("/data", false, stat));
        //System.out.println(s2);




        //回调功能
       /* client.getData("/data", false, new AsyncCallback.DataCallback() {
            @Override
           public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("123123123");
            }
        }, null);*/

        //创建节点
      // client.create("/data", "1".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);


//        client.exists("/data", new Watcher() {
//            @Override
//            public void process(WatchedEvent event) {
//                System.out.println("watch:" + event);
//            }
//        });

        System.in.read();
    }
}
