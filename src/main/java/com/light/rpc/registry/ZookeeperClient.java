package com.light.rpc.registry;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperClient implements Watcher {

    private final Logger logger = LoggerFactory.getLogger(RegistryService.class);

    public ZookeeperClient(String address, int timeout) {
        try {
            zooKeeper = new ZooKeeper(address, timeout, this);
            countDownLatch.await();
        } catch (Exception e) {
            logger.error("连接zk失败!", e);
            throw new RuntimeException("连接zk失败!");
        }
    }

    public ZooKeeper zooKeeper;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    /**
     * Zookeeper连接后，此处的State应当是CONECTING。连接中的时候去验证是否存在节点会报错。
     * 解决的方法也很简单，就是等到Zookeeper客户端以及完全连接上服务器，State为CONECTED之后再进行其他操作
     * 此处为如果接受到连接成功的event，则countDown，让当前线程继续其他事情。
     */
    @Override
    public void process(WatchedEvent event) {
        if (event.getState() == KeeperState.SyncConnected) {
            System.out.println("watcher received event");
            countDownLatch.countDown();
        }
    }

    public String create(String path, byte[] data, boolean persistent) throws KeeperException, InterruptedException {
        return this.zooKeeper.create(path, data, Ids.OPEN_ACL_UNSAFE, persistent ? CreateMode.PERSISTENT : CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public boolean exite(String path) throws KeeperException, InterruptedException {
        return null == zooKeeper.exists(path, this) ? false : true;
    }

    public List<String> getChildren(String path) throws KeeperException, InterruptedException {
        return this.zooKeeper.getChildren(path, false);
    }

    public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
        return this.zooKeeper.setData(path, data, version);
    }

    public byte[] getData(String path) throws KeeperException, InterruptedException {
        return this.zooKeeper.getData(path, false, null);
    }

    public void delete(String path, int version) throws InterruptedException, KeeperException {
        this.zooKeeper.delete(path, version);
    }

    public void closeConnect() throws InterruptedException {
        if (null != zooKeeper) {
            zooKeeper.close();
        }
    }

}
