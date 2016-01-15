package com.servicemesh.agility.distributed.sync;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * Provides distributed configuration data.
 */
public class DistributedConfig implements Watcher
{

    private final static Logger logger = Logger.getLogger(DistributedConfig.class);
    private static ZooKeeper _zooKeeper = null;

    public static synchronized ZooKeeper getZooKeeper()
    {
        try
        {
            if (_zooKeeper == null)
            {
                Properties properties = new Properties();
                File file = new File(System.getProperty("karaf.home") + "/etc/com.servicemesh.agility.distributed.sync.cfg");
                if (file.exists())
                {
                    properties.load(new FileInputStream(file));
                }

                Map<String, String> env = System.getenv();
                String url = properties.getProperty("zookeeper.url", "localhost:2181");
                String zookeeper_addr = env.get("ZOOKEEPER_PORT_2181_TCP_ADDR");
                String zookeeper_port = env.get("ZOOKEEPER_PORT_2181_TCP_PORT");
                if (zookeeper_addr != null && !zookeeper_addr.isEmpty())
                {
                    if (zookeeper_port != null && !zookeeper_port.isEmpty())
                    {
                        url = zookeeper_addr + ":" + zookeeper_port;
                    }
                    else
                    {
                        url = zookeeper_addr + ":2181";
                    }
                }

                String timeout = properties.getProperty("zookeeper.timeout", "10000");
                String retryCount = properties.getProperty("zookeeper.retryCount", "10");
                if (retryCount != null)
                {
                    ProtocolSupport.retryCount = Integer.parseInt(retryCount);
                }
                String retryDelay = properties.getProperty("zookeeper.retryDelay", "10000");
                if (retryDelay != null)
                {
                    ProtocolSupport.retryDelay = Long.parseLong(retryDelay);
                }
                logger.debug("Connecting to zookeeper at: " + url);
                _zooKeeper = new ZooKeeper(url, Integer.parseInt(timeout), new DistributedConfig());
                bumpZkHeartbeatPriority();
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
        }
        return _zooKeeper;
    }

    /**
     * Attempts to find the thread that Zookeeper spawned to send heartbeat messages to the ZooKeeper server and raise its
     * priority to MAX_PRIORITY. Warning - Fragile: dependent on ZooKeeper internals
     */
    private static void bumpZkHeartbeatPriority()
    {
        // iterate thru all Threads in current group & subgroups looking
        // for "SendThread"
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        int numThreads = group.activeCount();
        Thread[] threads = new Thread[numThreads * 2];
        numThreads = group.enumerate(threads, false);
        for (int i = 0; i < numThreads; i++)
        {
            Thread thread = threads[i];
            if (thread.getName().contains("SendThread"))
            { // see org.apache.zookeeper.ClientCnxn$SendThread
                int p = thread.getPriority();
                thread.setPriority(Thread.MAX_PRIORITY);
                logger.info("raised priority of Zookeeper heartbeat thread from " + p + " to " + thread.getPriority());
                return;
            }
        }
        logger.warn("could not find Zookeeper heartbeat thread to raise priority");
    }

    /**
     * Creates distributed configuration data.
     * 
     * @param path
     *            The path to the ZooKeeper node that will hold data
     * @param mode
     *            The ZooKeeper persistence mode for the node
     */
    public static void create(String path, CreateMode mode) throws Exception
    {
        create(getZooKeeper(), path, mode, null);
    }

    /**
     * Creates distributed configuration data.
     * 
     * @param path
     *            The path to the ZooKeeper node that will hold data
     * @param mode
     *            The ZooKeeper persistence mode for the node
     * @param watcher
     *            An implementation of the ZooKeeper Watcher interface to monitor data changes
     */
    public static void create(String path, CreateMode mode, Watcher watcher) throws Exception
    {
        create(getZooKeeper(), path, mode, watcher);
    }

    /**
     * Creates distributed configuration data.
     * 
     * @param zk
     *            A ZooKeeper object
     * @param path
     *            The path to the ZooKeeper node that will hold data
     * @param mode
     *            The ZooKeeper persistence mode for the node
     * @param watcher
     *            An implementation of the ZooKeeper Watcher interface to monitor data changes
     */
    public static void create(ZooKeeper zk, String path, CreateMode mode, Watcher watcher) throws Exception
    {
        ProtocolSupport ps = new ProtocolSupport(zk);
        StringBuilder sb = new StringBuilder();
        String[] dirs = path.split("/");
        for (String dir : dirs)
        {
            if (dir.length() > 0)
            {
                sb.append("/");
                sb.append(dir);
                CreateMode createMode = sb.toString().equals(path) ? mode : CreateMode.PERSISTENT;
                ps.ensureExists(sb.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
            }
        }

        if (watcher != null)
        {
            if (zk.exists(path, watcher) == null)
            {
                WatchedEvent event =
                        new WatchedEvent(Watcher.Event.EventType.NodeDeleted, Watcher.Event.KeeperState.SyncConnected, path);
                watcher.process(event);
            }
        }
    }

    /**
     * Acquires "ownership" of configuration data by virtue of being its creator.
     * 
     * @param path
     *            The path to the ZooKeeper node that will hold data
     * @return True if the path was created by this call
     */
    public static boolean acquire(String path)
    {
        return acquire(getZooKeeper(), path);
    }

    /**
     * Acquires "ownership" of configuration data by virtue of being its creator.
     * 
     * @param zk
     *            A ZooKeeper object
     * @param path
     *            The path to the ZooKeeper node that will hold data
     * @return True if the path was created by this call
     */
    public static boolean acquire(final ZooKeeper zk, final String path)
    {
        ProtocolSupport ps = new ProtocolSupport(zk);
        StringBuilder sb = new StringBuilder();
        String[] dirs = path.split("/");
        for (int i = 1; i < dirs.length - 1; i++)
        {
            String dir = dirs[i];
            sb.append("/");
            sb.append(dir);
            ps.ensureExists(sb.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        try
        {
            return ps.retryOperation(new ZooKeeperOperation() {
                @Override
                public boolean execute() throws KeeperException, InterruptedException
                {
                    try
                    {
                        zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                    }
                    catch (KeeperException.NodeExistsException ex)
                    {
                        return false;
                    }
                    return true;
                }
            });
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Returns true if configuration data exists.
     * 
     * @param path
     *            The path to an existing ZooKeeper node
     * @param watcher
     *            An implementation of the ZooKeeper Watcher interface to monitor data changes
     */
    public static boolean exists(final String path, final Watcher watcher)
    {
        return exists(getZooKeeper(), path, watcher);
    }

    /**
     * Returns true if configuration data exists.
     * 
     * @param zk
     *            A ZooKeeper object
     * @param path
     *            The path to an existing ZooKeeper node
     * @param watcher
     *            An implementation of the ZooKeeper Watcher interface to monitor data changes
     */
    public static boolean exists(final ZooKeeper zk, final String path, final Watcher watcher)
    {
        ProtocolSupport ps = new ProtocolSupport(zk);
        StringBuilder sb = new StringBuilder();
        String[] dirs = path.split("/");
        for (int i = 1; i < dirs.length - 1; i++)
        {
            String dir = dirs[i];
            sb.append("/");
            sb.append(dir);
            ps.ensureExists(sb.toString(), null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        try
        {
            return ps.retryOperation(new ZooKeeperOperation() {
                @Override
                public boolean execute() throws KeeperException, InterruptedException
                {
                    try
                    {
                        return zk.exists(path, watcher) != null;
                    }
                    catch (KeeperException.NodeExistsException ex)
                    {
                        return false;
                    }
                }
            });
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Returns children of a configuration data path
     * 
     * @param path
     *            The path to an existing ZooKeeper node
     * @return A list of the children nodes of the specified path.
     */
    public static List<String> getChildren(String path) throws Exception
    {
        return getChildren(getZooKeeper(), path, null);
    }

    /**
     * Returns children of a configuration data path
     * 
     * @param path
     *            The path to an existing ZooKeeper node
     * @param watcher
     *            An implementation of the ZooKeeper Watcher interface to monitor data changes
     * @return A list of the children nodes of the specified path.
     */
    public static List<String> getChildren(String path, Watcher watcher) throws Exception
    {
        return getChildren(getZooKeeper(), path, watcher);
    }

    /**
     * Returns children of a configuration data path
     * 
     * @param zk
     *            A ZooKeeper object
     * @param path
     *            The path to an existing ZooKeeper node
     * @param watcher
     *            An implementation of the ZooKeeper Watcher interface to monitor data changes
     * @return A list of the children nodes of the specified path.
     */
    public static List<String> getChildren(ZooKeeper zk, String path, Watcher watcher) throws Exception
    {
        List<String> children = null;
        try
        {
            children = zk.getChildren(path, watcher);
        }
        catch (KeeperException.NoNodeException ex)
        {
            children = new ArrayList<String>();
        }
        return children;
    }

    /**
     * Deletes distributed configuration data.
     * 
     * @param path
     *            The path to an existing ZooKeeper node
     */
    public static void delete(String path) throws Exception
    {
        delete(getZooKeeper(), path);
    }

    /**
     * Deletes distributed configuration data.
     * 
     * @param zk
     *            A ZooKeeper object
     * @param path
     *            The path to an existing ZooKeeper node
     */
    public static void delete(final ZooKeeper zk, final String path) throws Exception
    {
        try
        {
            ProtocolSupport ps = new ProtocolSupport(zk);
            ps.retryOperation(new ZooKeeperOperation() {
                @Override
                public boolean execute() throws KeeperException, InterruptedException
                {
                    zk.delete(path, -1);
                    return true;
                }
            });
        }
        catch (KeeperException.NoNodeException ex)
        {
        }
    }

    /**
     * Deletes children for a configuration data path
     * 
     * @param path
     *            The path to an existing ZooKeeper node
     */
    public static void deleteChildren(String path) throws Exception
    {
        deleteChildren(getZooKeeper(), path);
    }

    /**
     * Deletes children for a configuration data path
     * 
     * @param zk
     *            A ZooKeeper object
     * @param path
     *            The path to an existing ZooKeeper node
     */
    public static void deleteChildren(ZooKeeper zk, String path) throws Exception
    {
        try
        {
            List<String> children = zk.getChildren(path, false);
            for (String child : children)
            {
                String childPath = path + "/" + child;
                DistributedConfig.delete(zk, childPath);
            }
        }
        catch (KeeperException ex)
        {
            if (ex.code() != Code.NONODE)
            {
                logger.warn(ex.getMessage(), ex);
            }
        }
        catch (InterruptedException ex)
        {
        }
    }

    /**
     * This class's no-op implementation of the ZooKeeper Watcher interface.
     */
    @Override
    public void process(WatchedEvent event)
    {
        // TODO Auto-generated method stub

    }
}
