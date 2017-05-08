package org.mavadvise.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

    String netID,secAnswer;
    private DialogFragment saveDialog;
    int secQuestion;
    EditText fpNetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_credentials);
        fpNetId = (EditText) findViewById(R.id.fpNetId);
        //netID = fpNetId.getText().toString();

        Button next =  (Button) findViewById(R.id.goToSecurityPage);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUser();
            }
        });
    }

    private void navigateToNext(){
        Intent i = new Intent(ForgotPasswordCredentials.this,FPSecurity.class);
        i.putExtra("securityQuestionValue",secQuestion);
        i.putExtra("securityAnswer",secAnswer);
        i.putExtra("netId",netID);
        startActivity(i);
        finish();
    }

    private void getUser(){
        netID = fpNetId.getText().toString();
        saveDialog =  ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Fetching Details..");


        RequestBody formBody = new FormBody.Builder()
                .add("netID",netID)
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
                                    Log.i("securutyQuestionId",secAnswer);
                                    Log.i("securityques",""+secQuestion);
                                }catch (Exception e)
                                {
                                    Toast.makeText(getApplicationContext(), "Problem in extracting data", Toast.LENGTH_SHORT);
                                }

                                DialogFragment mDialog = AlertDialogFragment.newInstance();
                                mDialog.show(getFragmentManager(), "Got It");

                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                saveDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Sorry!!!The user doesnot exist",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

        urlResourceHelper.execute();
    }

    public static class AlertDialogFragment extends DialogFragment {
        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage("Found User. Answer Security Question")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((ForgotPasswordCredentials) getActivity()).navigateToNext();
                                }
                            }).create();
        }
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
