package org.mavadvise.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.URLResourceHelper;
import org.mavadvise.commons.Utils;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Register extends AppCompatActivity {

    private DialogFragment saveDialog;
    private AppConfig appConfig;

    private String firstName,
                    lastName,
                    email,
                    netID,
                    utaID,
                    branch,
                    role,
                    password,
                    passwordRtype,
                    securityAnswer;

    private int securityQuestionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Branch spinner
        Spinner spinner = (Spinner) findViewById(R.id.branchSP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.branch_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Role spinner
        spinner = (Spinner) findViewById(R.id.roleSP);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.role_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Security Questions spinner
        spinner = (Spinner) findViewById(R.id.securityQuestionSP);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.securityQuestions_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Register Button
        Button registerBtn = (Button) findViewById(R.id.registerBTN);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndRegisterUser();
            }
        });

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
    }

    private void validateAndRegisterUser(){
        firstName = ((EditText) findViewById(R.id.firstNameET)).getText().toString().trim();
        lastName = ((EditText) findViewById(R.id.lastNameET)).getText().toString().trim();
        email = ((EditText) findViewById(R.id.emailET)).getText().toString().trim();
        netID = ((EditText) findViewById(R.id.netIDET)).getText().toString().trim();
        utaID = ((EditText) findViewById(R.id.utaIDET)).getText().toString().trim();
        password = ((EditText) findViewById(R.id.passwordET)).getText().toString().trim();
        passwordRtype = ((EditText) findViewById(R.id.passwordRetypeET)).getText().toString().trim();
        securityAnswer = ((EditText) findViewById(R.id.securityAnswerET)).getText().toString().trim();

        Spinner branchSP = (Spinner) findViewById(R.id.branchSP);
        Spinner roleSP = (Spinner) findViewById(R.id.roleSP);
        Spinner securityQuestion = (Spinner) findViewById(R.id.securityQuestionSP);

        //Empty EditText Check
        if(firstName.length() == 0 ||
                lastName.length() == 0 ||
                email.length() == 0 ||
                netID.length() == 0 ||
                utaID.length() == 0 ||
                password.length() == 0 ||
                passwordRtype.length() == 0 ||
                securityAnswer.length() == 0){
            Toast.makeText(getApplicationContext(), "All fields are mandatory.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Spinner selection check
        if(branchSP.getSelectedItemPosition() == 0 ||
                roleSP.getSelectedItemPosition() == 0 ||
                securityQuestion.getSelectedItemPosition() == 0){
            Toast.makeText(getApplicationContext(), "All fields are mandatory.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Password retype check
        if(!password.equalsIgnoreCase(passwordRtype)){
            Toast.makeText(getApplicationContext(), "Passwords are not matching",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        branch = branchSP.getSelectedItem().toString();
        role = roleSP.getSelectedItem().toString();
        securityQuestionID = securityQuestion.getSelectedItemPosition();
        netID = netID.toLowerCase();

        //Register
        registerUser();
    }

    private void navigateToLogin(){
        finish();
    }

    private void registerUser(){
        saveDialog =  ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Saving...");

        password = Utils.hashString(password);

        RequestBody formBody = new FormBody.Builder()
                .add("firstName", firstName)
                .add("lastName", lastName)
                .add("email", email)
                .add("utaID", utaID)
                .add("netID", netID)
                .add("branch", branch)
                .add("roleType", role)
                .add("password", password)
                .add("securityQuestionID", "" + securityQuestionID)
                .add("securityAnswer", securityAnswer)
                .add("deviceID", appConfig.getFirebaseToken())
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("register", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();
                        DialogFragment mDialog = AlertDialogFragment.newInstance();
                        mDialog.show(getFragmentManager(), "Info");
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

    public static class AlertDialogFragment extends DialogFragment {
        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                .setMessage("Successfully registered.\nYou will need to Login now.")
                .setCancelable(false)
                .setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(
                                final DialogInterface dialog, int id) {
                            ((Register) getActivity()).navigateToLogin();
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
            dialog.setMessage("Saving details...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }
}
