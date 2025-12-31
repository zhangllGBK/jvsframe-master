package com.jarveis.frame.util;

import java.util.concurrent.*;

public class TestFuture {

    /*public static void main(String[] args) {
        //两个线程的线程池
        ExecutorService executor= Executors.newFixedThreadPool(2);
        //小红买酒任务，这里的future2代表的是小红未来发生的操作，返回小红买东西这个操作的结果
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()-> {
            System.out.println("爸：小红你去买瓶酒！");
            try {
                System.out.println("小红出去买酒了，女孩子跑的比较慢，估计5s后才会回来...");
                Thread.sleep(5000);
                return "我买回来了！";
            } catch (InterruptedException e) {
                System.err.println("小红路上遭遇了不测");
                return "来世再见！";
            }
        },executor);

        //小明买烟任务，这里的future1代表的是小明未来买东西会发生的事，返回值是小明买东西的结果
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->{
            System.out.println("爸：小明你去买包烟！");
            try {
                System.out.println("小明出去买烟了，可能要3s后回来...");
                Thread.sleep(3000);
                return "我买回来了!";
            } catch (InterruptedException e) {
                System.out.println("小明路上遭遇了不测！");
                return "这是我托人带来的口信，我已经不在了。";
            }
        },executor);

        //获取小红买酒结果，从小红的操作中获取结果，把结果打印
        future2.thenAccept((e)->{System.out.println("小红说："+e);});
        //获取小明买烟的结果
        future1.thenAccept((e)->{System.out.println("小明说："+e);});

        System.out.println("爸：loading......");
        System.out.println("爸:我觉得无聊甚至去了趟厕所。");
        System.out.println("爸：loading......");
    }*/

    public static void main(String[] args) {
        //第一种方式
        ExecutorService executor = Executors.newCachedThreadPool();
        Task task = new Task();
        FutureTask<Long> futureTask = new FutureTask<Long>(task);
        executor.submit(futureTask);
        executor.shutdown();

        //第二种方式，注意这种方式和第一种方式效果是类似的，只不过一个使用的是ExecutorService，一个使用的是Thread
        /*Task task = new Task();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(task);
        Thread thread = new Thread(futureTask);
        thread.start();*/

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e1) {
//            e1.printStackTrace();
//        }

        System.out.println("主线程在执行任务");

        try {
            //futureTask其实是Runnable+Future的综合体，因此可以通过futureTask.get()获取执行结果
            System.out.println("task运行结果"+futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("所有任务执行完毕");
    }
}

class Task implements Callable<Long> {
    @Override
    public Long call() throws Exception {
        System.out.println("子线程在进行计算");
        //Thread.sleep(3000);
        long sum = 0;
        for(int i=0;i<1000000000;i++)
            sum += i;
        return sum;
    }
}