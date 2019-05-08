package com.cn.distributied.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置中心，使用curator客户端
 *
 */
public class Config {

    private CuratorFramework client;
    public Config(){
        client = CuratorFrameworkFactory.newClient("localhost:2181",
                new RetryNTimes(3, 1000));
        client.start();
        init();
    }

    //本地缓存提高性能
    private Map<String,String> cache=new HashMap<>();
    private static final String CONFIG_PREFIX="/CONFIG";

    public void init(){
        try {
            List<String> childrenNames = client.getChildren().forPath(CONFIG_PREFIX);
            for (String name:childrenNames){
                String value = new String(client.getData().forPath(CONFIG_PREFIX+"/"+name));
                cache.put(name,value);
            }
            //监听事件
            //config子节点增加，删除，修改事件
            PathChildrenCache watch = new PathChildrenCache(client,CONFIG_PREFIX,true);
            watch.getListenable().addListener(new PathChildrenCacheListener() {
                @Override
                public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                    String path = event.getData().getPath();
                    String value = new String(event.getData().getData());
                    if (path.startsWith(CONFIG_PREFIX)) {
                        String key = path.substring(path.lastIndexOf("/")+1);
                        if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(event.getType())||
                                PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(event.getType())) {
                            cache.put(key,value);
                        } else if(PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(event.getType())){
                            cache.remove(key);
                        }
                    }
                }
            });
            watch.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //新增，更新
    public void save(String name,String value){
        //zk
        //cache

        String configFullName=CONFIG_PREFIX+"/"+name;
        try{
            Stat stat = client.checkExists().forPath(configFullName);       //节点是否存在
            if(stat==null){
                //父节点不存在创建父节点，创建永久节点
                client.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(configFullName,value.getBytes());
            }else {
                client.setData().forPath(configFullName,value.getBytes());
            }
            cache.put(name,value);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //watch


    public String get(String name){
        //cache
        return cache.get(name);
    }

}
