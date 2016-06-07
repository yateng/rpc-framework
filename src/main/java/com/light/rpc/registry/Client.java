package com.light.rpc.registry;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

/**
 * @author chenlian
 *
 */
public class Client {

    /**
     * @param args
     * @throws InterruptedException
     * @throws IOException
     * @throws KeeperException
     */
    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        ZookeeperClient baseZookeeper = new ZookeeperClient("192.168.155.108:2181", 2000);
        System.out.println("--------connect zookeeper ok-----------");

        // 获取某路径下所有节点
        List<String> children = baseZookeeper.getChildren("/registry/beijing");
        for (String child : children) {
            System.out.println(child);
            
        }
        System.out.println("-------------");
        
        
        
        baseZookeeper.closeConnect();
        // // 创建节点
        // byte [] data = {1, 2, 3, 4, 5};
        // String result = baseZookeeper.create("/test", data);
        // System.out.println(result);
        // System.out.println("--------create node ok-----------");
        //
        // // 获取某路径下所有节点
        // List<String> children = baseZookeeper.getChildren("/");
        // for (String child : children)
        // {
        // System.out.println(child);
        // }
        // System.out.println("--------get children ok-----------");
        //
        // // 获取节点数据
        // byte [] nodeData = baseZookeeper.getData("/test");
        // System.out.println(Arrays.toString(nodeData));
        // System.out.println("--------get node data ok-----------");
        //
        // // 更新节点数据
        // data = "test data".getBytes();
        // baseZookeeper.setData("/test", data, 0);
        // System.out.println("--------set node data ok-----------");
        //
        // nodeData = baseZookeeper.getData("/test");
        // System.out.println(Arrays.toString(nodeData));
        // System.out.println("--------get node new data ok-----------");
        //
        // baseZookeeper.closeConnect();
        // System.out.println("--------close zookeeper ok-----------");
    }

}
