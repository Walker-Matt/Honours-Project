package com.softwareeng.project.server;

/**
 * FULLY STATIC CLASS THAT STARTS THE PROGRAM OBJECT
 */
public class ServerMain {
    public static void main(String[] args) throws Exception {
    		new Server().start();
            //new Server("YOUR IP HERE", 9876)).start();
    }
}
