package org.mavadvise.app;

import org.mavadvise.data.User;

public class AppConfig {
    private String hostName = "10.0.2.2";
    private int port = 8080;

    private String sessionID;
    private String rememberMeToken;

    private static AppConfig instance;
    private User user;

    private AppConfig(){}

    public static  AppConfig getInstance(){
        if(instance == null) {
            instance = new AppConfig();
            instance.user = User.getUserInstance();
        }

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

    public String getRememberMeToken() {
        return rememberMeToken;
    }

    public void setRememberMeToken(String rememberMeToken) {
        this.rememberMeToken = rememberMeToken;
    }

    public User getUser() {
        return user;
    }

}
