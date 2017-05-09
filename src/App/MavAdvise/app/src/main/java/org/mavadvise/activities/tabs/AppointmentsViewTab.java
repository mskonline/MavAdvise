package org.mavadvise.activities.tabs;

/**
 * Created by Remesh on 4/12/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.mavadvise.R;
import org.mavadvise.adaptors.AppointmentsDataAdaptor;

public class AppointmentsViewTab extends Fragment {

    private JSONArray appointments;
    private AppointmentsDataAdaptor appointmentsDataAdaptor;

    public AppointmentsViewTab() {
    }

    public static AppointmentsViewTab newInstance() {
        return new AppointmentsViewTab();
    }

    public void refreshContent(JSONArray appointments) {
        appointmentsDataAdaptor.setAppointments(appointments);
        appointmentsDataAdaptor.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_view, container, false);
        ListView list = (ListView) rootView.findViewById(R.id.appointmentlist);

        appointmentsDataAdaptor = new AppointmentsDataAdaptor(appointments, this);
        list.setAdapter(appointmentsDataAdaptor);

        Button hist = (Button) rootView.findViewById(R.id.appHistoryBT);

        hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), AppointmentHistory.class);
                startActivity(i);
            }
        });

        return rootView;
    }
}
