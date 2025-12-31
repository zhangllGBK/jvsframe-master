package com.jarveis.frame.cache.store.jvscache;

import com.jarveis.frame.security.BASE64Cipher;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JvsCacheTools {

    public static void main(String[] args) {
        String path = "C:\\Users\\user\\.jvscache\\0.binlog";
        try {
            List<String[]> lines = readBinLog(path);
            for (int i = 0; i < lines.size(); i++) {
                int count = 1;
                String[] cmdsA = lines.get(i);
                for (int j = i + 1; j < lines.size(); j++) {
                    String[] cmdsB = lines.get(j);
                    if ("put".equalsIgnoreCase(cmdsA[0])
                            && cmdsA[0].equals(cmdsB[0])
                            && cmdsA[1].equals(cmdsB[1])
                            && cmdsA[2].equals(cmdsB[2])) {
                        break;
                    } else if ("hput".equalsIgnoreCase(cmdsA[0])
                            && cmdsA[0].equals(cmdsB[0])
                            && cmdsA[1].equals(cmdsB[1])
                            && cmdsA[2].equals(cmdsB[2])
                            && cmdsA[3].equals(cmdsB[3])) {
                        break;
                    } else {
                        // 计数
                        count ++;
                    }
                }

                if (count + i < lines.size()) {
                    // 说明未找到相同项
                    lines.set(i, null);
                }
            }

            writeBinLog(path, lines);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 从binlog中读取数据
     *
     * @param binLog
     * @return
     * @throws Exception
     */
    private static List<String[]> readBinLog(String binLog) throws Exception {
        List<String[]> lines = new ArrayList<>();

        File binLogFile = new File(binLog);
        if (!binLogFile.exists()) {
            return lines;
        }

        // 读取binlog
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
            lines.add(cmds);
        }
        br.close();

        return lines;
    }

    /**
     * 将优化后的数据写入到binlog
     *
     * @param binLog
     * @param lines
     * @throws Exception
     */
    private static void writeBinLog(String binLog, List<String[]> lines) throws Exception {
        File binLogFile = new File(binLog);
        if (!binLogFile.exists()) {
            return;
        }

        // 写入binlog
        BufferedWriter br = new BufferedWriter(new FileWriter(binLogFile));
        for (String[] line : lines) {
            if (line == null) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            for (Object arg : line) {
                sb.append(BASE64Cipher.encrypt(String.valueOf(arg))).append(",");
            }
            br.write(sb.substring(0, sb.length() - 1) + "\n");
        }
        br.close();
    }
}
