package org.mavadvise.activities;

import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import org.mavadvise.adaptors.AnnouncementDataAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.adaptors.AnnouncementDataAdapter;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import java.util.TimeZone;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Announcements extends AppCompatActivity {

    private JSONArray announcements;

    private AppConfig appConfig;
    String startDate,
            endDate,branch;
    boolean isChecked;
    Calendar c = Calendar.getInstance();
    private ProgressDialogHelper mDialog;
    private AnnouncementDataAdapter announcementsDataAdaptor;
    ListView announcementList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements);
        appConfig = ((MavAdvise)getApplication()).getAppConfig();

        Spinner spinner = (Spinner) findViewById(R.id.spinner_branch_announcement);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.branch_array_announcement, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if(spinner.getSelectedItemPosition() == 0)
            branch="all";
        else
            branch=spinner.getSelectedItem().toString();

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fab);

        if((appConfig.getUser().getRoleType()).equalsIgnoreCase("Advisor")){
            myFab.setVisibility(View.VISIBLE);
        }
        else{
            myFab.setVisibility(View.GONE);
        }

        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateToCreateNew();
            }
        });

        Button filter = (Button) findViewById(R.id.filterAnnoucementBTN);

        filter.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateToFilter();
            }
        });

        startDate = DateFormat.format("MM/dd/yyyy", c.getTimeInMillis()).toString();
        endDate = DateFormat.format("MM/dd/yyyy", c.getTimeInMillis()).toString();

        Log.i("startDate",startDate);
        Log.i("endDate",endDate);
        if(isChecked)
            Log.i("boolean","ischecked");
        else
            Log.i("notBoolean","notBoolean");

        announcementList = (ListView) findViewById(R.id.announcement_list);
        populateList();
        announcementsDataAdaptor = new AnnouncementDataAdapter(announcements,this);
        announcementList.setAdapter(announcementsDataAdaptor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent bundle) {
        Log.i("here","inOnActivity");
        if(requestCode==3){
            Log.i("here","inOnActivity2");
            if(resultCode == Activity.RESULT_OK){
                Log.i("here","inOnActivity3");
                startDate = bundle.getStringExtra("startDate");
                endDate = bundle.getStringExtra("endDate");
                if((appConfig.getUser().getRoleType()).equalsIgnoreCase("Advisor")){
                        String show= bundle.getStringExtra("myAnnouncement");
                        if(show.equalsIgnoreCase("yes"))
                            isChecked=true;
                        else
                            isChecked=false;
                }
                Log.i("startDate",startDate);
                Log.i("endDate",endDate);
                if(isChecked)
                    Log.i("boolean","ischecked");
                else
                    Log.i("notBoolean","notBoolean");
            }
            populateList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void navigateToCreateNew(){
        Intent intent = new Intent(Announcements.this, CreateAnnouncement.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToFilter(){

        Intent intent = new Intent(Announcements.this, FilterAnnouncements.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent,3);
    }

    private void populateList(){

        mDialog = ProgressDialogHelper.newInstance();
        mDialog.setMsg("Loading Announcements");

        RequestBody formBody;
        if(isChecked){
            formBody = new FormBody.Builder()
                    .add("startDate",startDate)
                    .add("endDate",endDate)
                    .add("branch",branch)
                    .add("netID", appConfig.getUser().getNetID())
                    .build();
        }
        else{
            formBody = new FormBody.Builder()
                    .add("startDate",startDate)
                    .add("endDate",endDate)
                    .add("branch",branch)
                    .build();
        }
        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("getAllAnnouncement", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                try {
                                    announcements = obj.getJSONArray("result");
                                    announcementsDataAdaptor.setAnnouncement(announcements);
                                    announcementsDataAdaptor.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                mDialog.dismiss();
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                mDialog.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        msg, Toast.LENGTH_LONG).show();
                            }
                        });

        urlResourceHelper.execute();



        announcementList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                try{
                    JSONObject obj = announcements.getJSONObject(position);
                    Intent i = new Intent(Announcements.this, ReadAnnouncements.class);
                    i.putExtra("announcementId",obj.getInt("announcementId"));
                    i.putExtra("date",obj.getString("date"));
                    i.putExtra("authorFirstName",obj.getString("firstName"));
                    i.putExtra("authorLastName",obj.getString("lastName"));
                    i.putExtra("message",obj.getString("message"));
                    i.putExtra("title",obj.getString("title"));
                    i.putExtra("netID",obj.getString("netId"));
                    startActivityForResult(i,2);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}