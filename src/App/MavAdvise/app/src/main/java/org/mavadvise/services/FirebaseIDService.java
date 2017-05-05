package org.mavadvise.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;

/**
 * Created by SaiKumar on 4/28/2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    AppConfig appConfig;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirebaseIDService", "Refreshed token: " + refreshedToken);

        saveRegistrationToken(refreshedToken);
    }

    private void saveRegistrationToken(String refreshedToken){
        appConfig = ((MavAdvise) getApplication()).getAppConfig();
        appConfig.setFirebaseToken(refreshedToken);
    }
}
