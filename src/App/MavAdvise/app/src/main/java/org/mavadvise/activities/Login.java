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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.Utils;
import org.mavadvise.data.User;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Login extends AppCompatActivity {

    private DialogFragment saveDialog;
    private AppConfig appConfig;

    String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button loginBtn = (Button) findViewById(R.id.loginBTN);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndLoginUser();
            }
        });

        Button forgotBtn = (Button) findViewById(R.id.forgotBTN);

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotUser();
            }
        });

        Button regBtn = (Button) findViewById(R.id.regBTN);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regUser();
            }
        });

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
    }

    private void validateAndLoginUser() {
        userName = ((EditText) findViewById(R.id.userNameET)).getText().toString().trim();
        password = ((EditText) findViewById(R.id.passET)).getText().toString().trim();

        if (userName.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "All fields are mandatory.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        saveDialog = Login.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Login");
        new LoginUser().execute();

    }

    private void navigateToDashboard(){
        Intent intent = new Intent(Login.this, DashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void forgotUser(){
        //Intent intent = new Intent(Login.this, Forgot.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        finish();
    }

    private void regUser(){
        Intent intent = new Intent(Login.this, Register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    private class LoginUser extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("Login", "Thread exception");
            }
            try

            {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(appConfig.getHostName())
                        .port(appConfig.getPort())
                        .addPathSegment("MavAdvise")
                        .addPathSegment("login")
                        .build();

                password = Utils.hashString(password);

                RequestBody formBody = new FormBody.Builder()
                        .add("netID", userName)
                        .add("password", password)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();

            } catch (
                    Exception e)

            {
                Log.e("HTTP Error", e.getMessage());
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            saveDialog.dismiss();

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("Login", "Thread exception");
            }


            if(result != null) {
                try {
                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                    String resultStr = obj.getString("type");
                    JSONObject resObj = obj.getJSONObject("result");

                    String resFirst = resObj.getString("firstName");
                    String resLast = resObj.getString("lastName");
                    String resEmail = resObj.getString("email");
                    String resRole = resObj.getString("roleType");
                    String resNet = resObj.getString("netID");
                    String resUta = resObj.getString("utaID");
                    String resBranch = resObj.getString("branch");

                    if(resultStr.equalsIgnoreCase("success")){
                        User user = appConfig.getUser();
                        user.setFirstName(resFirst);
                        user.setLastName(resLast);
                        user.setEmail(resEmail);
                        user.setDepartment(resBranch);
                        user.setNetID(resNet);
                        user.setUtaID(resUta);
                        user.setRoleType(resRole);

                        //DialogFragment mDialog = Login.AlertDialogFragment.newInstance();
                        //mDialog.show(getFragmentManager(), "Info");

                        navigateToDashboard();

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
                    .setMessage("You will be logged in.")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((Login) getActivity()).navigateToDashboard();
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
            dialog.setMessage("Logging in User...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }
}
