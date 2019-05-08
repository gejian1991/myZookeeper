package com.cn.client.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;


public class CuratorSessionExample {

    public static void main(String[] args) {
        final CuratorFramework client  = CuratorFrameworkFactory.newClient("localhost:2181", 1000, 1000, new RetryNTimes(1, 1000));

        client.start();

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState == ConnectionState.LOST) {
                    try {
                        //阻塞重新连接
                        if (client.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                            doTask();//重新执行任务

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        doTask();
    }


    public static void doTask() {
        //创建节点绑定监听器等操作

    }
}
