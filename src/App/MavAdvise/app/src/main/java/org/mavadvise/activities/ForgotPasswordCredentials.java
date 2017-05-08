package org.mavadvise.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.commons.URLResourceHelper;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ForgotPasswordCredentials extends AppCompatActivity {

    private String netID, secAnswer;
    private DialogFragment saveDialog;
    private int secQuestion;
    private EditText fpNetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_credentials);
        fpNetId = (EditText) findViewById(R.id.fpNetId);

        Button next = (Button) findViewById(R.id.goToSecurityPage);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                netID = fpNetId.getText().toString().toLowerCase();

                if(netID.length() == 0){
                    Toast.makeText(getApplicationContext(), "NetID is required", Toast.LENGTH_SHORT);
                    return;
                }

                getUser();
            }
        });
    }

    private void navigateToNext() {
        Intent i = new Intent(ForgotPasswordCredentials.this, FPSecurity.class);
        i.putExtra("securityQuestionValue", secQuestion);
        i.putExtra("securityAnswer", secAnswer);
        i.putExtra("netId", netID);
        startActivity(i);

        finish();
    }

    private void getUser() {
        saveDialog = ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching Details..");

        RequestBody formBody = new FormBody.Builder()
                .add("netID", netID)
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("getUser", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();
                        try {
                            JSONObject res = obj.getJSONObject("result");
                            secQuestion = res.getInt("securityQuestionID");
                            secAnswer = res.getString("securityAnswer");

                            navigateToNext();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Bad response from Server. Please try again", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg,
                                Toast.LENGTH_LONG).show();
                    }
                });

        urlResourceHelper.execute();
    }

    public static class ProgressDialogFragment extends DialogFragment {
        public static ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Checking...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }
}
