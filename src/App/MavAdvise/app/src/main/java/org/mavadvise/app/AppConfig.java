package org.mavadvise.app;

import org.json.JSONArray;
import org.mavadvise.data.User;

public class AppConfig {
    public static final String hostName = "10.0.2.2";
    public static final int port = 8080;

    private String rememberMeToken;
    private String firebaseToken;

    private static AppConfig instance;
    private User user;
    private JSONArray conflictingSessions;

    private AppConfig(){}

    public static  AppConfig getInstance(){
        if(instance == null) {
            instance = new AppConfig();
            instance.user = User.getUserInstance();
        }

        return instance;
    }

    public static final String
            SESSIONS_STIME_VALIDATION_ERR = "Start time cannot be in the past",
            SESSIONS_ETIME_VALIDATION_ERR = "End time cannot be before start time",
            SESSIONS_FREQ_VALIDATION_ERR = "Please select frequency",
            SESSIONS_SLOTS_VALIDATION_ERR = "Please enter No. of Slots",
            SESSIONS_LOCATION_VALIDATION_ERR = "Please enter the location",
            SESSIONS_ADD_SUCCESS = "Sessions added",
            SESSIONS_DELETE_ONLY_SCHD_ERR = "Only scheduled sessions with no appointments can be deleted.\nCancel it instead";

    public String getHostName() {
        return hostName;
    }

    public int getPort() {
        return port;
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

    public String getFirebaseToken() {
        if(firebaseToken == null)
            return "";

        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}
