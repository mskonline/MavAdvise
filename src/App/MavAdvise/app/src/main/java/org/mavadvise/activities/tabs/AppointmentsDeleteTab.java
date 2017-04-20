package org.mavadvise.activities.tabs;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.R;
import org.mavadvise.activities.ManageAppointments;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.AlertDialogHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.data.User;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppointmentsDeleteTab extends Fragment {

    private JSONArray appointments;

    private ListView deleteSessionsList;
    private AppointmentsViewTab.OptionsAdapter optionsAdapter;
    private ArrayList<Integer> selectedSessions = new ArrayList<Integer>();

    private AppConfig appConfig;
    private AlertDialogHelper alertDialog;

    private TextView deleteButton, cancelButton;
    private ProgressDialogHelper deleteDialog;

    public AppointmentsDeleteTab() {
    }

    public static AppointmentsDeleteTab newInstance() {
        AppointmentsDeleteTab fragment = new AppointmentsDeleteTab();
        return fragment;
    }

    public void refreshContent(JSONArray appointments){
        this.appointments = appointments;
        //optionsAdapter.notifyDataSetChanged();
    }
}
