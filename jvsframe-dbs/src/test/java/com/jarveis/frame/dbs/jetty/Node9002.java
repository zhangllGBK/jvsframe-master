package com.jarveis.frame.dbs.jetty;

import com.jarveis.frame.dbs.Application;
import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ant.DbsApplication;
import com.jarveis.frame.dbs.ant.DbsServer;

@DbsApplication
@DbsServer(httpPort = 9002)
public class Node9002 {

    public static void main(String[] args) {
        DbsCache.putConst(DbsConst.DBS_VERSION, "3.0.0");
        DbsCache.putConst(DbsConst.DBS_MACHINE, "005");
        DbsCache.putConst(DbsConst.DBS_NODE, "serviceNode");
        DbsCache.putConst(DbsConst.DBS_LOCAL, "http://127.0.0.1:9002");
        DbsCache.putConst(DbsConst.DBS_SERVICE_SERVER, "http://127.0.0.1:8001/dbs.service,http://127.0.0.1:8002/dbs.service");
        DbsCache.putConst(DbsConst.DBS_PUBLISH, "*");
        DbsCache.putConst(DbsConst.DBS_SUBSCRIBE, "*");

        Application.run(Node9002.class, args);
    }
}
