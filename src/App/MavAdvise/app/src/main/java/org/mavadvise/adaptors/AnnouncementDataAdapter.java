package org.mavadvise.adaptors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;

/**
 * Created by Gurleen on 5/1/2017.
 */

public class AnnouncementDataAdapter extends BaseAdapter {

    private JSONArray announcement;
    private LayoutInflater layoutInflater;
    private Context context;
    private int dColor;

    public AnnouncementDataAdapter(JSONArray announcement, Activity activity) {
        this.announcement = announcement;
        initResources(activity);
    }

    public void setAnnouncement(JSONArray announcement) {
        this.announcement = announcement;
    }

    @Override
    public int getCount() {
        return announcement != null ? announcement.length() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        int priority;


        if (row == null)
            row = layoutInflater.inflate(R.layout.list_announcement_item, parent, false);

        TextView title = (TextView) row.findViewById(R.id.announcement_title);
        TextView advisor = (TextView) row.findViewById(R.id.announcement_advisor);
        TextView branch = (TextView) row.findViewById(R.id.announcement_branch);
        TextView date = (TextView) row.findViewById(R.id.announcement_date);
        JSONObject obj = null;

        try {
            obj = announcement.getJSONObject(position);
            title.setText(obj.getString("title"));
            advisor.setText(obj.getString("firstName") + " " + obj.getString("lastName"));
            branch.setText(obj.getString("branch"));
            date.setText(obj.getString("date"));
            priority = obj.getInt("priority");
            if (priority == 1) {
                title.setTextColor(dColor);
            } else {
                title.setTextColor(Color.DKGRAY);
            }

        } catch (Exception e) {
            Toast.makeText(context, "Error in retrieving the list", Toast.LENGTH_SHORT);
        }

        return row;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private void initResources(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        context = activity.getApplicationContext();
        dColor = ResourcesCompat.getColor(activity.getResources(), R.color.colorAccent, null);
    }

}
