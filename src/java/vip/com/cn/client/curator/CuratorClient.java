package com.cn.client.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;


public class CuratorClient {

    public static void main(String[] args) throws Exception {
        //RetryNTimes重试的策略，1秒一次试三次,设置RetryPolicy不同实现，ExponentialBackoffRetry很复杂重试策略
        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181",
                new RetryNTimes(3, 1000));
        client.start();

        //创建临时节点
//        client.create().withMode(CreateMode.EPHEMERAL).forPath("/data", "3".getBytes());


        String path = "/data";
        NodeCache nodeCache = new NodeCache(client, path);
        nodeCache.start(false);//启动的时候没有去取最新的数据，所以首次打印
        //nodeCache.start(true);不打印
        //添加监听器，监听节点值的改变不是set动作
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                System.out.println("节点数据改变");
            }
        });


        //监听，与原生的一样是一次性的
        /*client.getData().usingWatcher(new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("用的是watch");
            }
        }).forPath(path);*/

        System.in.read();


    }
}
