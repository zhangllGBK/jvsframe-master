package com.jarveis.frame.dbs.jetty;

import com.jarveis.frame.dbs.Application;
import com.jarveis.frame.dbs.DbsCache;
import com.jarveis.frame.dbs.DbsConst;
import com.jarveis.frame.dbs.ant.DbsApplication;
import com.jarveis.frame.dbs.ant.DbsServer;

@DbsApplication
@DbsServer(httpPort = 8002)
public class Server8002 {

    public static void main(String[] args) {
        DbsCache.putConst(DbsConst.DBS_VERSION, "3.0.0");
        DbsCache.putConst(DbsConst.DBS_MACHINE, "002");
        DbsCache.putConst(DbsConst.DBS_NODE, "serviceServer");
        DbsCache.putConst(DbsConst.DBS_LOCAL, "http://127.0.0.1:8002");

        Application.run(Server8002.class, args);
    }
}
