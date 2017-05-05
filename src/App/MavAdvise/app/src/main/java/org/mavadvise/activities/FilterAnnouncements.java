package org.mavadvise.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.DatePickerHelper;
import org.mavadvise.commons.ProgressDialogHelper;

import java.util.Calendar;

public class FilterAnnouncements extends AppCompatActivity {

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate;

    private TextView startDateTV, endDateTV;
    private AppConfig appConfig;
    private ProgressDialogHelper saveDialog;
    String userType,show,startd,endd;
    boolean isChecked ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_announcements);

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
        initElements();

        Button okay = (Button) findViewById(R.id.applyFilterBTN);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAnnouncementPage();
            }
        });
    }

    private void navigateToAnnouncementPage() {
        //Intent intent = new Intent(FilterAnnouncements.this, Announcements.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent intent = getIntent();
        intent.putExtra("startDate", startd);
        intent.putExtra("endDate", endd);
        intent.putExtra("myAnnouncement",show);
        setResult(RESULT_OK,intent);
        //startActivity(intent);
        finish();
    }

    private void initElements() {

        userType = appConfig.getUser().getRoleType();

        startDateTV = (TextView) findViewById(R.id.startDateTVAnn);
        endDateTV = (TextView) findViewById(R.id.endDateTVAnn);
        Button s_Date = (Button) findViewById(R.id.changeStartDateBTAnn);
        Button e_Date = (Button) findViewById(R.id.changeEndDateBTAnn);
        CheckBox personal = (CheckBox) findViewById(R.id.checkBox_myAnnouncements);

        if (userType.equalsIgnoreCase("advisor")) {
            personal.setVisibility(View.VISIBLE);
        } else {
            personal.setVisibility(View.GONE);
        }

        endDate = Calendar.getInstance();


        s_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerHelper datePickerHelper = new DatePickerHelper();

                datePickerHelper.setOnSumbitListener(new DatePickerHelper.DatePickerListener() {
                    @Override
                    public void onDatePickerFinish(Calendar date) {
                        startDate = date;
                        endDate = date;
                        String d = DateFormat.format("MM/dd/yyyy", date.getTimeInMillis()).toString();
                        startd=d;
                        endd=d;
                        startDateTV.setText(d);
                        endDateTV.setText(d);
                    }
                });

                datePickerHelper.show(fm, "DatePick");
            }
        });

        e_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerHelper datePickerHelper = new DatePickerHelper();
                datePickerHelper.setMinDate(startDate);

                datePickerHelper.setOnSumbitListener(new DatePickerHelper.DatePickerListener() {
                    @Override
                    public void onDatePickerFinish(Calendar date) {
                        endDate = date;
                        String d = DateFormat.format("MM/dd/yyyy", date.getTimeInMillis()).toString();
                        endd=d;
                        endDateTV.setText(d);
                    }
                });

                datePickerHelper.show(fm, "DatePick");
            }
        });

        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isChecked = ((CheckBox) view).isChecked();
                if(isChecked){
                    show="yes";
                }
                else{
                    show="no";
                }


            }
        });
    }
}