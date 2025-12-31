package com.jarveis.frame.task.dbs;

import com.jarveis.frame.dbs.Application;
import com.jarveis.frame.dbs.ant.DbsApplication;

@DbsApplication
public class TaskStart {

	public static void main(String[] args){
		Application.run(TaskStart.class, args);
	}
}
