package com.jarveis.frame.cache.store.jvscache;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 配置对象
 *
 * @author liuguojun
 * @since 2024-03-05
 */
public class JvsConfig {

    private static final Logger log = LoggerFactory.getLogger(JvsConfig.class);

    private int maxSize;
    private String disk;
    /**
     * state: loading, running
     */
    private String state;

    public static final String STATE_LOADING = "loading";
    public static final String STATE_RUNNING = "running";

    public JvsConfig() {
        setMaxSize(-1);
        setDisk(System.getProperty("user.home") + File.separator + ".jvscache");
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        if (StringUtils.isNotEmpty(disk)) {
            this.disk = disk;
            File file = new File(this.disk);
            if (!file.exists()) {
                file.mkdir();
            }
            if (!file.isDirectory()) {
                try {
                    throw new FileNotFoundException("[" + disk + "] is not directory.");
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        if (STATE_LOADING.equals(state) || STATE_RUNNING.equals(state)) {
            this.state = state;
        }
    }
}
