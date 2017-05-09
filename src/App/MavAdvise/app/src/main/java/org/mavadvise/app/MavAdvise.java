package org.mavadvise.app;

import android.app.Application;
import android.content.Intent;

import org.mavadvise.activities.Login;

public class MavAdvise extends Application {

    private AppConfig appConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        this.initApp();
    }

    private void navigateToLogin() {
        Intent i = new Intent(getApplicationContext(), Login.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(i);
    }

    private void initApp() {
        appConfig = AppConfig.getInstance();
        appConfig.setSharedPreferences(getApplicationContext().getSharedPreferences("MavAdvisePrefs", MODE_PRIVATE));
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
