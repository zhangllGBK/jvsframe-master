package com.jarveis.frame.dbs.jetty;


import com.jarveis.frame.dbs.Application;
import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ant.DbsApplication;
import com.jarveis.frame.dbs.ant.DbsServer;

@DbsApplication
@DbsServer(httpPort = 9001)
public class Node9001 {

    public static void main(String[] args) {
        DbsCache.putConst(DbsConst.DBS_VERSION, "3.0.0");
        DbsCache.putConst(DbsConst.DBS_MACHINE, "004");
        DbsCache.putConst(DbsConst.DBS_NODE, "serviceNode");
        DbsCache.putConst(DbsConst.DBS_LOCAL, "http://127.0.0.1:9001");
        DbsCache.putConst(DbsConst.DBS_SERVICE_SERVER, "http://127.0.0.1:8001/dbs.service");
        DbsCache.putConst(DbsConst.DBS_PUBLISH, "\\d{5}");
        DbsCache.putConst(DbsConst.DBS_SUBSCRIBE, "\\d{5}");

        Application.run(Node9001.class, args);
    }
}
