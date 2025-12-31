package com.jarveis.frame.dbs.jetty;

public class Dbs4JettyTest {

    public static void main(String[] args) {
        Dbs4Jetty dbs4Jetty = new Dbs4Jetty();
        dbs4Jetty.setHttpPort(8000);
        dbs4Jetty.start();
    }
}