
package org.mavadvise.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.URLResourceHelper;
import org.w3c.dom.Text;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ReadAnnouncements extends AppCompatActivity {

    private AppConfig appConfig;
    String firstName,
            lastName,
            date,
            netId,
            message,
            title;
    int announcementId;
    DialogFragment saveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_announcements);

        appConfig = ((MavAdvise) getApplication()).getAppConfig();

        Bundle b = getIntent().getExtras();

//        i.putExtra("announcementId",obj.getInt("announcementId"));
//        i.putExtra("date",obj.getString("date"));
//        i.putExtra("authorFirstName",obj.getString("firstName"));
//        i.putExtra("authorLastName",obj.getString("lastName"));
//        i.putExtra("message",obj.getString("message"));
//        i.putExtra("title",obj.getString("title"));
//        i.putExtra("netID",obj.getString("netId"));

        firstName = b.getString("authorFirstName");
        lastName = b.getString("authorLastName");
        netId = b.getString("netID");
        date = b.getString("date");
        title = b.getString("title");
        message = b.getString("message");
        announcementId = b.getInt("announcementId");


        TextView titleView = (TextView) findViewById(R.id.announcementTitle_view);
        TextView msgView = (TextView) findViewById(R.id.announcementMsg_view);
        TextView advisorView = (TextView) findViewById(R.id.announcementAdvisor_view);
        TextView dateView = (TextView) findViewById(R.id.announcementDate_view);
        Button delView = (Button) findViewById(R.id.deleteAnnouncementBTN);
        delView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAnnouncement(announcementId);
            }
        });

        if(appConfig.getUser().getNetID().equalsIgnoreCase(netId)){
            advisorView.setVisibility(View.GONE);
            delView.setVisibility(View.VISIBLE);
        }

        if(!appConfig.getUser().getNetID().equalsIgnoreCase(netId)){
            delView.setVisibility(View.GONE);
        }

        titleView.setText(title);
        msgView.setText(message);
        advisorView.setText(firstName + " "+ lastName);
        dateView.setText(date);

    }

    public static class ProgressDialogFragment extends DialogFragment {

        public static ProgressDialogFragment newInstance() {
            return new ProgressDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Deleting Announcement...");
            dialog.setIndeterminate(true);

            return dialog;
        }
    }

    private void deleteAnnouncement(int announcementId){

        saveDialog = ProgressDialogFragment.newInstance();
        saveDialog.show(getFragmentManager(),"Deleting..");

        RequestBody formBody = new FormBody.Builder()
                .add("announcementID", "" +announcementId).build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("deleteAnnouncement", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                saveDialog.dismiss();
                                DialogFragment mDialog = AlertDialogFragment.newInstance();
                                mDialog.show(getFragmentManager(), "Info");
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                saveDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Couldn't Delete Announcement. Please try again later",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

        urlResourceHelper.execute();

    }

    public static class AlertDialogFragment extends DialogFragment {
        public static AlertDialogFragment newInstance() {
            return new AlertDialogFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage("Successfully Deleted.")
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        final DialogInterface dialog, int id) {
                                    ((ReadAnnouncements)getActivity()).navigateBack();
                                }
                            }).create();
        }
    }

    private void navigateBack(){
        finish();
    }
}
