package org.mavadvise.activities.tabs;

/**
 * Created by Remesh on 4/12/2017.
 */

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.text.format.DateFormat;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
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

public class AppointmentsAddTab extends Fragment{

    View thisView;

    private AppConfig appConfig;
    private ProgressDialogHelper saveDialog;

    private JSONArray sessions;

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

    private void initControls(View view){

    }

}
