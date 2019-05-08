package com.cn.distributied.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.test.ClientBase;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class zkLock {
    private ThreadLocal<ZooKeeper> zooKeeper=new ThreadLocal<>();

    private String LOCK_NAME="/lock";

    private ThreadLocal<String> currentNodeName=new ThreadLocal<>();
    //ThreadLocal threadLocal=new ThreadLocal();
    public void init(){

        if(zooKeeper.get()==null){
            try {
                zooKeeper.set(new ZooKeeper("localhost:2181", 3000, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {

                    }
                }));
                //创建节点/lock
                //zooKeeper.get().create(LOCK_NAME,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void lock(){

        init();
        if (tryLock()){
            System.out.println("==========已经获取到锁==============");
        }
    }
    public  boolean tryLock(){
        //临时顺序节点名字
        String nodeName=LOCK_NAME+"/zk_";
        try {
            //    /lock/zk_1
            currentNodeName.set(zooKeeper.get().create(nodeName,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));
            //获取父节点所有子节点
            List<String> list=zooKeeper.get().getChildren(LOCK_NAME,false);//zk_1,zk_2.。。。。
            Collections.sort(list);

            String minNodeName = list.get(0);
            if (currentNodeName.get().equals(LOCK_NAME+"/"+minNodeName)){
                return true;
            }else {
                //监听当前节点的前一个节点
                String currentNodeSimpleName=currentNodeName.get().substring(currentNodeName.get().lastIndexOf("/")+1);
                Integer currentNodeIndex = list.indexOf(currentNodeSimpleName);
                String preNodeName = list.get(currentNodeIndex-1);

                //监听，阻塞
                final CountDownLatch countDownLatch=new CountDownLatch(1);
                zooKeeper.get().exists(LOCK_NAME + "/" + preNodeName, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if(event.getType().equals(Event.EventType.NodeDeleted)){
                            System.out.println(Thread.currentThread().getName()+"被唤醒状态");
                            countDownLatch.countDown();
                        }
                    }
                });
                System.out.println(Thread.currentThread().getName()+"阻塞状态");
                countDownLatch.await();
                //threadLocal.set(currentNodeName);
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void unlock(){
        //String currentNodeName=(String) threadLocal.get();
        //threadLocal.remove();
        try {
            zooKeeper.get().delete(currentNodeName.get(),-1);//将版本设置为-1，进行强制删除
            currentNodeName.set(null);
            zooKeeper.get().close();                                  //看场景关不关
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
}
