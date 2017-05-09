package org.mavadvise.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mavadvise.R;

public class Notification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent i = getIntent();

        String msg = i.getStringExtra("msg");
        String msgEx = i.getStringExtra("msgEx");

        TextView notificationMsg = (TextView) findViewById(R.id.notificationMsg);
        TextView notificationMsgExtra = (TextView) findViewById(R.id.notificationMsgExtra);

        notificationMsg.setText(msg);

        if(msgEx != null)
            notificationMsgExtra.setText(msgEx);

        Button notificationClsBtn = (Button) findViewById(R.id.notificationClsBtn);
        notificationClsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
