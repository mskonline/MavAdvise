package org.mavadvise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.data.User;

import java.util.ArrayList;

public class DashBoard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private User user;
    private AppConfig appConfig;
    private ArrayList<String> options;
    private ListView optionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        appConfig = ((MavAdvise)getApplication()).getAppConfig();
        user = appConfig.getUser();

        setUpDashboardLayout();

        if(user.getRoleType().equalsIgnoreCase("student"))
            setUpStudentOptions();
        else
            setUpAdvisorOptions();
    }

    private void setUpStudentOptions(){
        options = new ArrayList<String>(){{
            add("Appointments");
            add("Announcements");
        }};

        // Configure list view
        optionsList = (ListView) findViewById(R.id.dashboardLV);
        optionsList.setAdapter(new OptionsAdapter());

        setUpStudentOptionsListener();
    }

    private void setUpStudentOptionsListener(){
        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = null;

                switch (position){
                    case 0:
                        i = new Intent(view.getContext(), ManageAppointments.class);
                        break;
                    case 1:
                        i=new Intent(view.getContext(), Announcements.class);
                        break;
                }

                startActivity(i);
            }
        });

    }

    private void setUpAdvisorOptions(){
        options = new ArrayList<String>(){{
                add("Manage Sessions");
                add("Start a Session");
                add("Announcements");
        }};

        // Configure list view
        optionsList = (ListView) findViewById(R.id.dashboardLV);
        optionsList.setAdapter(new OptionsAdapter());

        setUpAdvisorOptionsListener();
    }

    private void setUpAdvisorOptionsListener(){
        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = null;

                switch (position){
                    case 0:
                        i = new Intent(view.getContext(), ManageSessions.class);
                        break;
                    case 1:
                        i = new Intent(view.getContext(), StartSession.class);
                        break;
                    case 2:
                        i=new Intent(view.getContext(), Announcements.class);
                        break;
                }

                startActivity(i);
            }
        });
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.list_dashboard_option, parent, false);
            TextView optionText;

            optionText = (TextView) row.findViewById(R.id.optiontext);
            optionText.setText(options.get(position));

            return row;
        }

        public OptionsAdapter(){}

        public int getCount() {
            return options.size();
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }
    }

    private void setUpDashboardLayout(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        TextView userNameTV = (TextView) headerView.findViewById(R.id.usernameNavTV);
        TextView userNameEmail = (TextView) headerView.findViewById(R.id.userEmailNavTV);

        userNameTV.setText(user.getFirstName() + " " + user.getLastName());
        userNameEmail.setText(user.getEmail());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            Intent intent = new Intent(DashBoard.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_change_password) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
