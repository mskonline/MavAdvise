package org.mavadvise.app;

public class AppConfig {
    private String hostName = "localhost";
    private int port = 8080;
    private String sessionID;

    private static AppConfig instance;

    private AppConfig(){
    }

    public static  AppConfig getInstance(){
        if(instance == null)
            instance = new AppConfig();

        return instance;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }
}
