package org.mavadvise.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
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
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.URLResourceHelper;
import org.mavadvise.commons.Utils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ChangePassword extends AppCompatActivity {

    private AppConfig appConfig;
    private EditText oldpwd,
            newpwd,
            confpwd;
    private String oldPassword,
            newPassword,
            confirmPassword;
    private DialogFragment saveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        appConfig = ((MavAdvise) getApplication()).getAppConfig();

        initElement();
    }

    private void initElement() {
        oldpwd = (EditText) findViewById(R.id.cpOldPassword);
        newpwd = (EditText) findViewById(R.id.cpNewPassword);
        confpwd = (EditText) findViewById(R.id.cpConfirmPassword);

        Button change = (Button) findViewById(R.id.cpresetPassword);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        oldPassword = oldpwd.getText().toString();
        newPassword = newpwd.getText().toString();
        confirmPassword = confpwd.getText().toString();
        oldPassword = Utils.hashString(oldPassword);

        if (newPassword.equals(confirmPassword)) {
            confirmPassword = Utils.hashString(confirmPassword);
            saveDialog = ProgressDialogFragment.newInstance();
            saveDialog.show(getFragmentManager(), "Saving changes...");

            RequestBody formBody = new FormBody.Builder()
                    .add("netID", appConfig.getUser().getNetID())
                    .add("oldPassword", oldPassword)
                    .add("newPassword", confirmPassword)
                    .build();

            URLResourceHelper urlResourceHelper =
                new URLResourceHelper("changePassword", formBody,
                    new URLResourceHelper.onFinishListener() {
                        @Override
                        public void onFinishSuccess(JSONObject obj) {
                            saveDialog.dismiss();
                            DialogFragment mDialog = AlertDialogFragment.newInstance();
                            mDialog.show(getFragmentManager(), "Got It");

                        }

                        @Override
                        public void onFinishFailed(String msg) {
                            saveDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Problems in saving data",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

            urlResourceHelper.execute();
        } else {
            Toast.makeText(getApplicationContext(), "Password does not match",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    public static class AlertDialogFragment extends DialogFragment {
        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage("Password changed successfully!!!")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((ChangePassword) getActivity()).navigateToLogin();
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
            dialog.setMessage("Saving changes...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ChangePassword.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
