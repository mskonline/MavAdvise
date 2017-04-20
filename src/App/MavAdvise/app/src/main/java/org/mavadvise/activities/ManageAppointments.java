package org.mavadvise.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.R;
import org.mavadvise.activities.tabs.AppointmentsAddTab;
import org.mavadvise.activities.tabs.AppointmentsDeleteTab;
import org.mavadvise.activities.tabs.AppointmentsViewTab;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ManageAppointments extends AppCompatActivity {

    private ManageAppointments.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private AppConfig appConfig;
    private DialogFragment mDialog;

    private JSONArray appointments;

    private AppointmentsViewTab appointmentsViewTab;
    private AppointmentsDeleteTab appointmentsDeleteTab;
    private AppointmentsAddTab appointmentsAddTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Final","here");
        setContentView(R.layout.activity_manage_appointments);


        setUpTabLayout();

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
        mDialog =  ManageAppointments.ProgressDialogFragment.newInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mDialog.isAdded())
            mDialog.show(getSupportFragmentManager(), "Loading");

        refreshSessionsData();
    }

    private void refreshSessionsData(){
        new ManageAppointments.AppointmentsData().execute();
    }

    private class AppointmentsData extends AsyncTask<String, Void , String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(500);
            } catch (Exception e) {}

            try {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(appConfig.getHostName())
                        .port(appConfig.getPort())
                        .addPathSegment("MavAdvise")
                        .addPathSegment("getAppointments")
                        .build();

                //String sessionId = SessionManager.getInstance().getSessionId();

                RequestBody formBody = new FormBody.Builder()
                        .add("netID", appConfig.getUser().getNetID())
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        //.addHeader("Cookie",sessionId)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();
            } catch (Exception e) {
                Log.e("HTTP Error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if(result != null) {
                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                    appointments = obj.getJSONArray("result");
                    if(appointments !=null){
                        Log.i("no","not null");
                    }
                    Log.i("In", "In post execute");
                    Log.i("jso", obj.getString("result"));

                    appointmentsViewTab.refreshContent(appointments);
                    appointmentsDeleteTab.refreshContent(appointments);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error retrieving the sessions.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e){
                Log.e("JSON Parse", e.getMessage());
            }

            mDialog.dismiss();
        }
    }

    public void showViewTab(){
        mViewPager.setCurrentItem(1);
    }

    private void setUpTabLayout(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarapp);
        setSupportActionBar(toolbar);
        Log.i("First", "reached first");

        mSectionsPagerAdapter = new ManageAppointments.SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.containerapp);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        Log.i("Hello", "Reached here");

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.apptabs);
        tabLayout.setupWithViewPager(mViewPager);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment instance = null;

            switch (position) {
                case 0:
                    appointmentsAddTab =  AppointmentsAddTab.newInstance();
                    instance = appointmentsAddTab;
                    break;
                case 1:
                    appointmentsViewTab =  AppointmentsViewTab.newInstance();
                    instance = appointmentsViewTab;
                    break;
                case 2:
                    appointmentsDeleteTab = AppointmentsDeleteTab.newInstance();
                    instance = appointmentsDeleteTab;
                    break;
            }

            return instance;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ADD";
                case 1:
                    return "VIEW";
                case 2:
                    return "DELETE";
            }
            return null;
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static ManageAppointments.ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading Appointments...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }
}
