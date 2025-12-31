package com.jarveis.frame.cache;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.jarveis.frame.config.ApplicationConfig;

public class CacheTester {

    public static void main(String[] args) {

        Puddle config = new Puddle();
        config.parse();

        System.setProperty("java.net.preferIPv4Stack", "true"); // Disable IPv6 in JVM

        CacheChannel cache = Puddle.getChannel();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        do {
            try {
                System.out.print("> ");
                System.out.flush();

                String line = in.readLine().trim();
                if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                    break;
                }

                String[] cmds = line.split(" ");
                if ("get".equalsIgnoreCase(cmds[0])) {
                    CacheObject obj = cache.get(cmds[1], cmds[2]);
                    System.out.printf("[%s,%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getLevel(),
                            obj.getValue());
                } else if ("put".equalsIgnoreCase(cmds[0])) {
                    cache.put(cmds[1], cmds[2], cmds[3]);
                    System.out.printf("[%s,%s]<=%s\n", cmds[1], cmds[2], cmds[3]);
                } else if ("remove".equalsIgnoreCase(cmds[0])) {
                    cache.remove(cmds[1], cmds[2]);
                    System.out.printf("[%s,%s]=>null\n", cmds[1], cmds[2]);
                } else if ("clear".equalsIgnoreCase(cmds[0])) {
                    cache.clear(cmds[1]);
                    System.out.printf("Cache [%s] clear.\n", cmds[1]);
                } else if ("lpush".equalsIgnoreCase(cmds[0])) {
                    cache.lpush(cmds[1], cmds[2], cmds[3]);
                    System.out.printf("[%s,%s]<=%s\n", cmds[1], cmds[2], cmds[3]);
                } else if ("rpush".equalsIgnoreCase(cmds[0])) {
                    cache.rpush(cmds[1], cmds[2], cmds[3]);
                    System.out.printf("[%s,%s]<=%s\n", cmds[1], cmds[2], cmds[3]);
                } else if ("lpop".equalsIgnoreCase(cmds[0])) {
                    CacheObject obj = cache.lpop(cmds[1], cmds[2]);
                    System.out.printf("[%s,%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getLevel(), obj.getValue());
                } else if ("rpop".equalsIgnoreCase(cmds[0])) {
                    CacheObject obj = cache.rpop(cmds[1], cmds[2]);
                    System.out.printf("[%s,%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getLevel(), obj.getValue());
                } else if ("lrange".equalsIgnoreCase(cmds[0])) {
                    CacheObject obj = cache.lrange(cmds[1], cmds[2], Integer.parseInt(cmds[3]));
                    System.out.printf("[%s,%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getLevel(), obj.getValue());
                } else if ("llen".equalsIgnoreCase(cmds[0])) {
                    CacheObject obj = cache.llen(cmds[1], cmds[2]);
                    System.out.printf("[%s,%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getLevel(), obj.getValue());
                } else if ("lrem".equalsIgnoreCase(cmds[0])) {
                    cache.lrem(cmds[1], cmds[2], Integer.parseInt(cmds[3]));
                    System.out.printf("[%s,%s]已清除\n", cmds[1], cmds[2]);
                } else if ("hput".equalsIgnoreCase(cmds[0])) {
                    cache.hput(cmds[1], cmds[2], cmds[3], cmds[4]);
                    System.out.printf("[%s,%s$%s]<=%s\n", cmds[1], cmds[2], cmds[3], cmds[4]);
                } else if ("hget".equalsIgnoreCase(cmds[0])) {
                    CacheObject obj = cache.hget(cmds[1], cmds[2], cmds[3]);
                    System.out.printf("[%s,%s$%s,L%d]=>%s\n", obj.getRegion(), obj.getKey(), obj.getField(), obj.getLevel(), obj.getValue());
                } else if ("hrem".equalsIgnoreCase(cmds[0])) {
                    cache.hrem(cmds[1], cmds[2], cmds[3]);
                    System.out.printf("[%s,%s$%s]已清除\n", cmds[1], cmds[2], cmds[3]);
                } else if ("help".equalsIgnoreCase(cmds[0])) {
                    printHelp();
                } else {
                    System.out.println("Unknown command.");
                    printHelp();
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Wrong arguments.");
                printHelp();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (true);

        cache.close();

        System.exit(0);
    }

    private static void printHelp() {
        System.out.println("Usage: [cmd] region key [value]");
        System.out.println("cmd: get/put/remove/quit/exit/help");
    }
}
