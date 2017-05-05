package org.mavadvise.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CreateAnnouncement extends AppCompatActivity {

    private String title,message,netID;
    int priorityLevel;
    private AppConfig appConfig;
    private Date date;


    private DialogFragment saveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_announcement);

        Spinner spinner = (Spinner) findViewById(R.id.spinner_priority_announcement);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.priority, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final Button postAnnouncementBtn = (Button) findViewById(R.id.postannouncemenntBTN);
        postAnnouncementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAnnouncement();

            }

        });
        appConfig = ((MavAdvise) getApplication()).getAppConfig();
    }

    private void navigateToAnnouncementPage(){
        Intent intent = new Intent(CreateAnnouncement.this, Announcements.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void postAnnouncement(){

        title= ((EditText) findViewById(R.id.titleET_createAnnouncement)).getText().toString().trim();
        message= ((EditText) findViewById(R.id.messageET_createAnnouncement)).getText().toString().trim();

        Spinner priority = (Spinner) findViewById(R.id.spinner_priority_announcement);

        if((message.length() == 0) || (title.length() == 0)){
            Toast.makeText(getApplicationContext(), "All fields are mandatory.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(priority.getSelectedItemPosition() == 0){

            Toast.makeText(getApplicationContext(), "All fields are mandatory.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        priorityLevel=priority.getSelectedItemPosition();

        if(priorityLevel != 1){
            priorityLevel=0;
        }

        registerAnnouncement();
    }

    private void registerAnnouncement(){
        saveDialog =  Register.ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(), "Posting...");
        date=new Date();

        SimpleDateFormat fromDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = fromDateFormat.format(new Date());
        netID= appConfig.getUser().getNetID();

        //http://localhost:8080/MavAdvise/createAnnouncement?
        // message=hello there&date=2017-04-21&priority=0&title=helllio there&netID=bxs1234

        RequestBody formBody = new FormBody.Builder()
                .add("message",message)
                .add("date",dateStr)
                .add("priority", "" +priorityLevel)
                .add("title",title)
                .add("netID",netID)
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("createAnnouncement", formBody,
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
                    .setMessage("Your Announcement has been posted successfully")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((CreateAnnouncement) getActivity()).navigateToAnnouncementPage();
                                }
                            }).create();
        }
    }
}
