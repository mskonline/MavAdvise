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
import org.mavadvise.commons.Utils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class FPNewPassword extends AppCompatActivity {

    private String newPassword,
            confirmPassword,
            netId;
    private DialogFragment saveDialog;
    private EditText newPass,
            confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fpnew_password);

        Bundle b = getIntent().getExtras();
        netId = b.getString("netId");
        Log.i("netId in fpnewpass",netId);
        initElement();


    }

    private void initElement(){
        newPass = (EditText) findViewById(R.id.fpNewPassword);
        confirmPass = (EditText) findViewById(R.id.fpConfirmPassword);
        Button reset = (Button) findViewById(R.id.resetPassword);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatePassword();
            }
        });

    }

    private void navigateToLogin(){
        Intent intent = new Intent(FPNewPassword.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void updatePassword(){

        newPassword = newPass.getText().toString();
        confirmPassword = confirmPass.getText().toString();

        if(newPassword.equals(confirmPassword)){

            saveDialog =  ProgressDialogFragment.newInstance();
            saveDialog.show(getFragmentManager(), "Saving changes...");

            confirmPassword = Utils.hashString(confirmPassword);

            RequestBody formBody = new FormBody.Builder()
                    .add("netID",netId)
                    .add("password",confirmPassword)
                    .build();
            URLResourceHelper urlResourceHelper =
                    new URLResourceHelper("updatePassword", formBody,
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
        }else{
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
                                    ((FPNewPassword) getActivity()).navigateToLogin();
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
            dialog.setMessage("Making changes....");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }

}
