package com.jarveis.frame.cache.store.jvscache;

import com.jarveis.frame.security.BASE64Cipher;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存管理
 *
 * @author liuguojun
 * @since 2024-03-06
 */
public class JvsCacheManager {

    private final static ConcurrentHashMap<String, JvsCache> caches =  new ConcurrentHashMap<>();

    private final static JvsConfig config = new JvsConfig();

    public static JvsCache getCache(String region) {
        JvsCache cache = caches.get(region);
        return cache;
    }

    public static void putCache(String region, JvsCache jvsCache) {
        caches.put(region, jvsCache);
    }

    public static void setDisk(String disk){
        if (StringUtils.isNotEmpty(disk)) {
            config.setDisk(disk);
        }
    }

    public static String getDisk() {
        return config.getDisk();
    }

    public static void setMaxSize(int maxSize) {
        config.setMaxSize(maxSize);
    }

    public static int getMaxSize() {
        return config.getMaxSize();
    }

    public static String getState() {
        return config.getState();
    }

    /**
     * 加载binlog文件
     *
     * @throws Exception
     */
    public static void loadBinLog() throws Exception {
        config.setState(JvsConfig.STATE_LOADING);
        String indexPath = config.getDisk() + File.separator + "index.binlog";
        File indexFile = new File(indexPath);
        if (indexFile.exists()) {
            // 获取index数据
            BufferedReader br = new BufferedReader(new FileReader(indexFile));
            String name;
            while (true) {
                name = br.readLine();
                if (name == null) {
                    break;
                }
                String binLogPath = config.getDisk() + File.separator + name + ".binlog";
                File binLogFile = new File(binLogPath);
                if (binLogFile.exists()) {
                    loadBinLog(binLogFile);
                }
            }
            br.close();
        }
        config.setState(JvsConfig.STATE_RUNNING);
    }

    /**
     * 加载binlog文件
     *
     * @param binLogFile
     * @throws Exception
     */
    private static void loadBinLog(File binLogFile) throws Exception {
        // 加载指令，写入到缓存
        BufferedReader br = new BufferedReader(new FileReader(binLogFile));
        String commandLine;
        String[] cmds;
        while (true) {
            commandLine = br.readLine();
            if (commandLine == null) {
                break;
            }
            // 解析指令
            cmds = commandLine.split(",");
            for (int i = 0; i < cmds.length; i++) {
                cmds[i] = BASE64Cipher.decrypt(cmds[i]);
            }
            loadCache(cmds);
        }
        br.close();
    }

    /**
     * 根据操作指令，装载缓存
     *
     * @param cmds
     */
    private static void loadCache(String[] cmds) {
        JvsCache cache = getCache(cmds[1]);
        if (cache == null) {
            cache = new JvsCache(cmds[1]);
            JvsCacheManager.putCache(cmds[1], cache);
        }

        if ("get".equalsIgnoreCase(cmds[0])) {
            cache.get(cmds[2]);
        } else if ("put".equalsIgnoreCase(cmds[0])) {
            cache.put(cmds[2], cmds[3]);
        } else if ("remove".equalsIgnoreCase(cmds[0])) {
            cache.remove(cmds[2]);
        } else if ("clear".equalsIgnoreCase(cmds[0])) {
            cache.clear();
        } else if ("lpush".equalsIgnoreCase(cmds[0])) {
            cache.lpush(cmds[2], cmds[3]);
        } else if ("rpush".equalsIgnoreCase(cmds[0])) {
            cache.rpush(cmds[2], cmds[3]);
        } else if ("lpop".equalsIgnoreCase(cmds[0])) {
            cache.lpop(cmds[2]);
        } else if ("rpop".equalsIgnoreCase(cmds[0])) {
            cache.rpop(cmds[2]);
        } else if ("lrange".equalsIgnoreCase(cmds[0])) {
            cache.lrange(cmds[2], Integer.parseInt(cmds[3]));
        } else if ("llen".equalsIgnoreCase(cmds[0])) {
            cache.llen(cmds[2]);
        } else if ("lrem".equalsIgnoreCase(cmds[0])) {
            cache.lrem(cmds[2], Integer.parseInt(cmds[3]));
        } else if ("hput".equalsIgnoreCase(cmds[0])) {
            cache.hput(cmds[2], cmds[3], cmds[4]);
        } else if ("hget".equalsIgnoreCase(cmds[0])) {
            cache.hget(cmds[2], cmds[3]);
        } else if ("hrem".equalsIgnoreCase(cmds[0])) {
            cache.hrem(cmds[2], cmds[3]);
        }
    }

}
