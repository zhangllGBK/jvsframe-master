package com.jarveis.frame.cache.store.jvscache;

import com.jarveis.frame.security.BASE64Cipher;
import com.jarveis.frame.util.CharacterUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 操作日志
 * <pre>
 *     问题：
 *     由于缓存的操作日志都记录在了binlog文件中，会造成日志文件的冗余；
 *     因为加载缓存时，是根据binlog文件来进行的；随机文件的操作日志增加，加载时间也会随之增加。
 *     而最开始的操作日志并不对最终的缓存数据造成影响，可以说是无效指令；比如：
 *     put("name", "tom")
 *     put("name", "jerry")
 *     put("name", "snoopy")
 *     put("name", "spike")
 *     put("name", "tyke")
 *     put("name", "jerry")
 *     其实最终的 name 数据应该是 jerry
 *     但是关于put name 的指令就有 5 条冗余；
 *     TODO 后期可以增加一个优化线程，分析其中的冗余数据进行优化
 * </pre>
 *
 * @author liuguojun
 * @since 2024-03-05
 */
public class JvsBinLog extends Thread {

    private static final Logger log = LoggerFactory.getLogger(JvsBinLog.class);

    private JvsCache cache;
    private Queue<String> commandQueue;
    private BufferedOutputStream bos;
    private long binLogLines = -1;

    public JvsBinLog(JvsCache cache) {
        this.cache = cache;
        this.commandQueue = new ConcurrentLinkedQueue<>();
        try {
            createBinLog(true);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    /**
     * 创建日志文件
     * <pre>
     *     用于记录缓存的操作日志
     * </pre>
     *
     * @param isInit
     * @throws Exception
     */
    private void createBinLog(boolean isInit) throws Exception  {
        String index = "0";
        String indexPath = JvsCacheManager.getDisk() + File.separator + "index.binlog";
        File indexFile = new File(indexPath);
        if (!indexFile.exists()) {
            indexFile.createNewFile();
            FileUtils.write(indexFile, index + "\n", CharacterUtil.UTF8);
        } else {
            // 获取最后一行
            List<String> lines = FileUtils.readLines(indexFile);
            if (!lines.isEmpty()) {
                // 当缓存启动是，加载binlog（读） 和 binlog线程（写）同时运行，有可能第57行，向里面写的数据还没有进入index.binlog文件，读已开始了；
                index = lines.get(lines.size() - 1);
            }
            if (!isInit) {
                index = String.valueOf(NumberUtils.toInt(index) + 1);
                FileUtils.write(indexFile, index + "\n", CharacterUtil.UTF8, true);
            }
        }

        String binLogPath = JvsCacheManager.getDisk() + File.separator + index + ".binlog";
        File binLogFile = new File(binLogPath);
        if (!binLogFile.exists()) {
            binLogFile.createNewFile();
        }

        if (this.bos != null) {
            this.bos.flush();
            this.bos.close();
        }

        this.bos = new BufferedOutputStream(new FileOutputStream(binLogFile, true));
        this.binLogLines = Files.lines(Paths.get(binLogPath)).count();
    }

    /**
     * 添加指令
     *
     * @param command
     * @param args
     */
    public void addCommandQueue(String command, Object ...args) {
        if (JvsConfig.STATE_RUNNING.equals(JvsCacheManager.getState())) {
            StringBuilder sb = new StringBuilder();
            sb.append(BASE64Cipher.encrypt(command)).append(",");
            for (Object arg : args) {
                sb.append(BASE64Cipher.encrypt(String.valueOf(arg))).append(",");
            }
            this.commandQueue.offer(sb.substring(0, sb.length() - 1) + "\n");
        }
    }

    public void run() {
        while (true) {
            try {
                boolean isFlush = false;

                if (binLogLines > 1000000) {
                    // 重新创建binlog文件
                    createBinLog(false);
                }

                while (!commandQueue.isEmpty()){
                    String command = commandQueue.poll();
                    this.bos.write(command.getBytes(CharacterUtil.UTF8));
                    this.binLogLines++;
                    isFlush = true;
                }
                if (isFlush) {
                    // 将缓存中的数据，写入到文件
                    this.bos.flush();
                }

                // 暂停1秒
                Thread.sleep(1000l);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

}
