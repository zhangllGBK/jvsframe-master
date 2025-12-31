package com.jarveis.frame.task;

/**
 * @desc 任务测试用例
 * @author liuguojun
 * @create 2018-02-24
 */
public class EchoTask extends ITask {
	
	private static int count = 0;
	
	private String[] arr = {"Hello", "Tom", "Welcome", "Beijing"};

	public void execute() {
		this.getTaskInfo().getParam();
		System.out.println(arr[count % arr.length]);
		count ++;
	}

}
