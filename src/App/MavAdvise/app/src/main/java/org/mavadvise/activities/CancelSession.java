package org.mavadvise.activities;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.AlertDialogHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.Utils;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CancelSession extends AppCompatActivity {

    private AppConfig appConfig;
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

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
    }

    private void validateAndCancel(){
        EditText cancelReasonET = (EditText) findViewById(R.id.cancelReasonET);
        cancelReason = cancelReasonET.getText().toString().trim();

        if(cancelReason.length() == 0){

            return;
        }

        cancelDialog =  ProgressDialogHelper.newInstance();
        cancelDialog.setMsg("Cancelling...");
        cancelDialog.show(getSupportFragmentManager(), "Cancel");

        new CancelSessionTask().execute();
    }

    private class CancelSessionTask extends AsyncTask<Void, Void , String> {

        @Override
        protected String doInBackground(Void... params){

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("CancelSessionTask", "Thread exception");
            }

            try {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(appConfig.getHostName())
                        .port(appConfig.getPort())
                        .addPathSegment("MavAdvise")
                        .addPathSegment("cancelSession")
                        .build();

                RequestBody formBody = new FormBody.Builder()
                        .add("sessionID", "" + sessionID)
                        .add("cancelReason", cancelReason)
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
            cancelDialog.dismiss();

            try {
                Thread.sleep(500);
            } catch (Exception e){
                Log.e("CancelSessionTask", "Thread exception");
            }

            if(result != null) {
                try {
                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                    String resultStr = obj.getString("type");

                    if(resultStr.equalsIgnoreCase("success")){
                        Intent i = getIntent();
                        i.putExtra("status","C");
                        setResult(RESULT_OK,i);
                        finish();
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
}
