package org.mavadvise.activities.tabs;

/**
 * Created by Remesh on 4/12/2017.
 */

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.DialogFragment;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.ListFragment;
        import android.text.format.DateFormat;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.RelativeLayout;;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONObject;
        import org.json.JSONTokener;
        import org.mavadvise.R;
        import org.mavadvise.activities.ManageAppointments;
        import org.mavadvise.app.AppConfig;
        import org.mavadvise.app.MavAdvise;
        import org.mavadvise.commons.AdvisorPickerHelper;
        import org.mavadvise.commons.DatePickerHelper;
        import org.mavadvise.commons.ProgressDialogHelper;
        import org.mavadvise.commons.TimePickerHelper;
        import org.mavadvise.data.User;

        import okhttp3.FormBody;
        import okhttp3.HttpUrl;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

public class AppointmentsAddTab extends Fragment {

    View thisView;

    private AppConfig appConfig;
    private DialogFragment mDialog;
    private ProgressDialogHelper saveDialog;

    private JSONArray appointments, advisors;
    private RelativeLayout repeatRL;
    TextView dateTV;

    TextView advTV;

    private Calendar appDate = Calendar.getInstance();

    public AppointmentsAddTab() {
    }

    public static AppointmentsAddTab newInstance() {
        AppointmentsAddTab fragment = new AppointmentsAddTab();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_add, container, false);

        initControls(rootView);

        thisView = rootView;

        appConfig = ((MavAdvise) getActivity().getApplication()).getAppConfig();
        return rootView;
    }

    private void initControls(View view) {

        dateTV = (TextView) view.findViewById(R.id.DateTV);

        advTV = (TextView) view.findViewById(R.id.AdvTV);

        appDate = Calendar.getInstance();
        dateTV.setText(DateFormat.format("MM/dd/yyyy", appDate.getTimeInMillis()).toString());

        Button dateBtn = (Button) view.findViewById(R.id.ChangeDateBT);
        Button advBtn = (Button) view.findViewById(R.id.SelectAdvBT);

        repeatRL = (RelativeLayout) view.findViewById(R.id.appAddRL);


        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                DatePickerHelper datePickerHelper = new DatePickerHelper();

                datePickerHelper.setOnSumbitListener(new DatePickerHelper.DatePickerListener() {
                    @Override
                    public void onDatePickerFinish(Calendar date) {
                        appDate = date;
                        String d = DateFormat.format("MM/dd/yyyy", date.getTimeInMillis()).toString();
                        dateTV.setText(d);
                    }
                });

                datePickerHelper.show(fm, "DatePick");
            }
        });

        advBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i("me", "Clicked");
                setAdvisorlist();
                FragmentManager fm = getFragmentManager();
                AdvisorPickerHelper advisorPicker = new AdvisorPickerHelper();
                advisorPicker.setAdvisors(advisors);
                Log.i("me6", "Clicked6");

                advisorPicker.setOnSelectListener(new AdvisorPickerHelper.AdvisorPickerListener() {
                    @Override
                    public void onAdvisorPickerFinish(String adv) {
                        Log.i("me1", "Clicked1");
                        advTV.setText(adv);
                    }
                });


            }
        });

    }

    private void setAdvisorlist() {
        new AppointmentsAddTab.AdvisorsData().execute();
    }

    private class AdvisorsData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }

            try {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(appConfig.getHostName())
                        .port(appConfig.getPort())
                        .addPathSegment("MavAdvise")
                        .addPathSegment("getAdvisors")
                        .build();

                RequestBody formBody = new FormBody.Builder()
                        .add("branch", appConfig.getUser().getDepartment())
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
                if (result != null) {
                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                    advisors = obj.getJSONArray("result");
                    if (advisors != null) {
                        Log.i("no", "not null");
                    }
                    Log.i("In", "In post execute");
                    Log.i("jso", obj.getString("result"));

                } else {
                    Toast.makeText(getContext(),
                            "Error retrieving the sessions.", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e("JSON Parse", e.getMessage());
            }
 //           mDialog.dismiss();
        }
    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static AppointmentsAddTab.ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }


    }
}
