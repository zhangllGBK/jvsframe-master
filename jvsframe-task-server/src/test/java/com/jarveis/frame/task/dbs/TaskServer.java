package com.jarveis.frame.task.dbs;

import com.jarveis.frame.dbs.Application;
import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ant.DbsApplication;
import com.jarveis.frame.dbs.ant.DbsServer;

@DbsApplication
@DbsServer(httpPort = 8001)
public class TaskServer {

	public static void main(String[] args){
		DbsCache.putConst(DbsConst.DBS_VERSION, "3.0.0");
		DbsCache.putConst(DbsConst.DBS_MACHINE, "001");
		DbsCache.putConst(DbsConst.DBS_NODE, DbsConst.DBS_NODE_SERVER);
		DbsCache.putConst(DbsConst.DBS_LOCAL, "http://127.0.0.1:8001");
		DbsCache.putConst(DbsConst.DBS_SELF_CHECK_TIME, "60000");

		Application.run(TaskServer.class, args);
	}
}
