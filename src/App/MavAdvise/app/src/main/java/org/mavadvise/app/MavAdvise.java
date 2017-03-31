package org.mavadvise.app;

import android.app.Application;


public class MavAdvise extends Application {

    private AppConfig appConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        this.initApp();
    }

    private void initApp(){
        appConfig = AppConfig.getInstance();
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
