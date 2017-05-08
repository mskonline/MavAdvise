package org.mavadvise.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.activities.tabs.SessionsAddTab;
import org.mavadvise.activities.tabs.SessionsDeleteTab;
import org.mavadvise.activities.tabs.SessionsViewTab;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ManageSessions extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private AppConfig appConfig;
    private ProgressDialogHelper mDialog;

    private JSONArray sessions;

    private SessionsViewTab sessionsViewTab;
    private SessionsDeleteTab sessionsDeleteTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_sessions);

        setUpTabLayout();

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
        mDialog = ProgressDialogHelper.newInstance();
        mDialog.setMsg("Loading sessions...");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mDialog.isAdded())
            mDialog.show(getSupportFragmentManager(), "Loading");

        try {
            Thread.sleep(500);
        } catch (Exception e) {
        }

        getSessionsData();
    }

    public void refreshSessionsData(JSONArray sessions) {
        this.sessions = sessions;
        sessionsViewTab.refreshContent(this.sessions);
        sessionsDeleteTab.refreshContent(this.sessions);
    }

    private void getSessionsData() {
        RequestBody formBody = new FormBody.Builder()
                .add("netID", appConfig.getUser().getNetID())
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("getSessions", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                try {
                                    sessions = obj.getJSONArray("result");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (sessionsViewTab == null || sessionsDeleteTab == null) {
                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception e) {
                                    }
                                }

                                sessionsViewTab.refreshContent(sessions);
                                sessionsDeleteTab.refreshContent(sessions);

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
    }

    public void showViewTab() {
        mViewPager.setCurrentItem(1);
    }

    private void setUpTabLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
                    instance = SessionsAddTab.newInstance();
                    break;
                case 1:
                    sessionsViewTab = SessionsViewTab.newInstance();
                    instance = sessionsViewTab;
                    getSessionsData();
                    break;
                case 2:
                    sessionsDeleteTab = SessionsDeleteTab.newInstance();
                    instance = sessionsDeleteTab;
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
}
