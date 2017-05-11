package org.mavadvise.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.URLResourceHelper;
import org.mavadvise.commons.Utils;
import org.mavadvise.data.User;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Login extends AppCompatActivity {

    private DialogFragment saveDialog;
    private AppConfig appConfig;

    private String userName, password;

    private InputMethodManager imm;

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
                forgotPassword();
            }
        });

        Button regBtn = (Button) findViewById(R.id.regBTN);
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void validateAndLoginUser() {
        userName = ((EditText) findViewById(R.id.userNameET)).getText().toString().trim();
        password = ((EditText) findViewById(R.id.passET)).getText().toString().trim();

        if (userName.length() == 0 || password.length() == 0) {
            Toast.makeText(getApplicationContext(), "All fields are mandatory.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        doLogin();
    }

    private void doLogin(){
        saveDialog = Login.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Login");
        password = Utils.hashString(password);

        RequestBody formBody = new FormBody.Builder()
                .add("netID", userName)
                .add("password", password)
                .add("deviceID", appConfig.getPreferences(AppConfig.DEVICE_ID))
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("login", formBody, new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();

                        try {
                            JSONObject resObj = obj.getJSONObject("result");

                            String resFirst = resObj.getString("firstName");
                            String resLast = resObj.getString("lastName");
                            String resEmail = resObj.getString("email");
                            String resRole = resObj.getString("roleType");
                            String resNet = resObj.getString("netID");
                            String resUta = resObj.getString("utaID");
                            String resBranch = resObj.getString("branch");

                            User user = appConfig.getUser();
                            user.setFirstName(resFirst);
                            user.setLastName(resLast);
                            user.setEmail(resEmail);
                            user.setDepartment(resBranch);
                            user.setNetID(resNet);
                            user.setUtaID(resUta);
                            user.setRoleType(resRole);

                            navigateToDashboard();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();
                        Toast.makeText(getApplicationContext(), msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        urlResourceHelper.execute();
    }

    private void navigateToDashboard(){
        Intent intent = new Intent(Login.this, DashBoard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void forgotPassword(){
        Intent intent = new Intent(Login.this, ForgotPasswordCredentials.class);
        startActivity(intent);
    }

    private void registerUser(){
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    public static class ProgressDialogFragment extends DialogFragment {
        public static ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Logging in...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }
}
