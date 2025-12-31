package com.jarveis.frame.dbs.jetty;


import com.jarveis.frame.dbs.Application;
import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ant.DbsApplication;
import com.jarveis.frame.dbs.ant.DbsServer;

@DbsApplication
@DbsServer(httpPort = 9000)
public class Node9000 {

    public static void main(String[] args) {
        DbsCache.putConst(DbsConst.DBS_VERSION, "3.2.0");
        DbsCache.putConst(DbsConst.DBS_MACHINE, "001");
        DbsCache.putConst(DbsConst.DBS_NODE, "serviceNode");
        DbsCache.putConst(DbsConst.DBS_LOCAL, "http://127.0.0.1:9000");

        System.setProperty("dbs.wsEnable", "true");

        Application.run(Node9000.class, args);
    }
}
