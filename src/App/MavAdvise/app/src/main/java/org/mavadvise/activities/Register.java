package org.mavadvise.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.Utils;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

        //Register
        saveDialog =  ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Saving...");
        new RegisterUser().execute();
    }

    private void navigateToLogin(){
        Intent intent = new Intent(Register.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }

    private class RegisterUser extends AsyncTask<Void, Void , String> {

        @Override
        protected String doInBackground(Void... params){

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("Register", "Thread exception");
            }

            try {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(appConfig.getHostName())
                        .port(appConfig.getPort())
                        .addPathSegment("MavAdvise")
                        .addPathSegment("register")
                        .build();

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
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();
            } catch (Exception e){
                Log.e("HTTP Error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            saveDialog.dismiss();

            try {
                Thread.sleep(500);
            } catch (Exception e){
                Log.e("Register", "Thread exception");
            }

            if(result != null) {
                try {
                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                    String resultStr = obj.getString("type");

                    if(resultStr.equalsIgnoreCase("success")){
                        DialogFragment mDialog = AlertDialogFragment.newInstance();
                        mDialog.show(getFragmentManager(), "Info");
                    } else {
                        String msg = obj.getString("message");
                        Toast.makeText(getApplicationContext(), msg,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("JSON Parse", e.getMessage());
                }
            }

        }
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
