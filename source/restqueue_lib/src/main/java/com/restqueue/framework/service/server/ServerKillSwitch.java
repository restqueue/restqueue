package com.restqueue.framework.service.server;

/**
 * User: Nik Tomkinson
 * Date: 17/09/2013
 * Time: 20:19
 */
public class ServerKillSwitch {
    private static final Object lock = new Object();
    private static ServerKillSwitch instance = new ServerKillSwitch();

    private volatile boolean serverSetToStop =false;

    public static ServerKillSwitch getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ServerKillSwitch();
                }
            }
        }
        return instance;
    }

    private ServerKillSwitch() {
    }

    public void killServer(){
        instance.serverSetToStop =true;
    }

    public boolean isServerSetToStop() {
        return serverSetToStop;
    }
}
