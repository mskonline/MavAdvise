package org.mavadvise.app;

import org.json.JSONArray;
import org.mavadvise.data.User;

public class AppConfig {
    private String hostName = "10.0.2.2";//"a90c0256.ngrok.io";
    private int port = 8080;

    private String sessionID;
    private String rememberMeToken;

    private static AppConfig instance;
    private User user;
    private JSONArray conflictingSessions;

    private AppConfig(){}

    public static  AppConfig getInstance(){
        if(instance == null) {
            instance = new AppConfig();
            instance.user = User.getUserInstance();

            //TODO - remove
            instance.user.setFirstName("Sai Kumar");
            instance.user.setLastName("Kumar");
            instance.user.setNetID("sxm6131");
            instance.user.setEmail("saikumar.manakan@mavs.uta.edu");
            instance.user.setRoleType("advisor");
        }

        return instance;
    }

    public static final String
            SESSIONS_STIME_VALIDATION_ERR = "Start time cannot be in the past",
            SESSIONS_ETIME_VALIDATION_ERR = "End time cannot be before start time",
            SESSIONS_FREQ_VALIDATION_ERR = "Please select frequency",
            SESSIONS_SLOTS_VALIDATION_ERR = "Please enter No. of Slots",
            SESSIONS_ADD_SUCCESS = "Sessions added",
            SESSIONS_DELETE_ONLY_SCHD_ERR = "Only scheduled sessions with no appointments can be deleted.\nCancel it instead";

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

    public JSONArray getConflictingSessions() {
        return conflictingSessions;
    }

    public void setConflictingSessions(JSONArray conflictingSessions) {
        this.conflictingSessions = conflictingSessions;
    }
}
