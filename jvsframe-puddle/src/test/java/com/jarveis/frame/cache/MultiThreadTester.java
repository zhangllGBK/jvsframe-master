package com.jarveis.frame.cache;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.jarveis.frame.config.ApplicationConfig;

public class MultiThreadTester {
	
	public static void main(String[] args) throws InterruptedException {

		Puddle config = new Puddle();
		config.parse();

		final CacheChannel cache = Puddle.getChannel();

		final String region = "const";
		// key, msg, name
		final String key = "name";
		final String value = "tom";

		ExecutorService threadPool = Executors.newCachedThreadPool();

		long ct = System.currentTimeMillis();

		for (int i = 1; i <= 1000; i++) {
			final int seq = i;
			threadPool.execute(new Runnable() {
				@Override
				public void run() {
					String name = "Thread-" + seq;
					for (int j = 1; j <= 1000; j++) {
						cache.put(region, key + "-" + seq + "-" + j, value + "-" + seq + "-" + j);
					}
				}
			});
		}
		threadPool.shutdown();

		System.out.println("times = " + (System.currentTimeMillis() - ct));
	}
}
