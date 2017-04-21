package org.mavadvise.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.mavadvise.R;
import org.mavadvise.adaptors.SessionsDataAdaptor;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;

public class SessionsAddError extends AppCompatActivity {

    AppConfig appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions_add_error);

        appConfig = ((MavAdvise) getApplication()).getAppConfig();

        ListView sessionErrorTV = (ListView) findViewById(R.id.sessionsAddErrorLV);
        Button okBtn = (Button) findViewById(R.id.sessionokBT);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appConfig.setConflictingSessions(null);
                finish();
            }
        });

        SessionsDataAdaptor sessionsDataAdaptor = new SessionsDataAdaptor(appConfig.getConflictingSessions(), this, true);
        sessionErrorTV.setAdapter(sessionsDataAdaptor);
    }
}
