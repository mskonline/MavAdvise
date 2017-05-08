package org.mavadvise.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.commons.AlertDialogHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CancelSession extends AppCompatActivity {

    private String cancelReason;
    private int sessionID;
    private ProgressDialogHelper cancelDialog;

    private AlertDialogHelper alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_session);

        Button cancelBT = (Button) findViewById(R.id.sessionCBT);
        Bundle extras = getIntent().getExtras();
        sessionID = extras.getInt("sessionID");

        cancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog = AlertDialogHelper.newInstance("Do you want cancel this session ?",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                validateAndCancel();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //do Nothing
                            }
                        });
                alertDialog.setCancelable(false);
                alertDialog.show(getSupportFragmentManager(), "Alert");
            }
        });
    }

    private void validateAndCancel() {
        EditText cancelReasonET = (EditText) findViewById(R.id.cancelReasonET);
        cancelReason = cancelReasonET.getText().toString().trim();

        if (cancelReason.length() == 0) {
            return;
        }

        doCancelSession();
    }

    private void doCancelSession() {
        cancelDialog = ProgressDialogHelper.newInstance();
        cancelDialog.setMsg("Cancelling...");
        cancelDialog.show(getSupportFragmentManager(), "Cancel");

        RequestBody formBody = new FormBody.Builder()
                .add("sessionID", "" + sessionID)
                .add("cancelReason", cancelReason)
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("cancelSession", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                Intent i = getIntent();
                                i.putExtra("status", "C");
                                setResult(RESULT_OK, i);
                                finish();
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                Toast.makeText(getApplicationContext(), msg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

        urlResourceHelper.execute();
    }
}
