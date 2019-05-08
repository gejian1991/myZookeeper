package com.cn.client.curator;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.retry.RetryNTimes;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 不是真正的选举，只是一个用临时文件顺序存入数据最小的当leader
 */
public class LeaderLatchExample {

    public static void main(String[] args) throws Exception {
        //clients客户端机器集合
        List<CuratorFramework> clients = Lists.newArrayList();

        List<LeaderLatch> leaderLatches = Lists.newArrayList();
        //模拟十个机器，客户端
        for(int i=0; i<10; i++) {
            CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(1,1000));
            clients.add(client);
            client.start();
            //创建临时节点，里面每个机器会顺序往临时节点加入数据
            LeaderLatch leaderLatch = new LeaderLatch(client, "/LeaderLatch", "client#"+i);

            leaderLatches.add(leaderLatch);
            leaderLatch.start();
        }

        TimeUnit.SECONDS.sleep(5);

        //找领导
        for (LeaderLatch leaderLatch: leaderLatches) {
            if (leaderLatch.hasLeadership()) {
                System.out.println("当前Leader是"+ leaderLatch.getId());
                break;
            }
        }

        System.in.read();

        for (CuratorFramework curatorFramework: clients) {
            curatorFramework.close();
        }

    }
}
