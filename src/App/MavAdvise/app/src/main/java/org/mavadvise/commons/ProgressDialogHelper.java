package org.mavadvise.commons;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by SaiKumar on 4/13/2017.
 */

public class ProgressDialogHelper extends DialogFragment {

    public static ProgressDialogHelper newInstance() {
        return new ProgressDialogHelper();
    }

    private String msg = "Saving...";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final android.app.ProgressDialog dialog = new android.app.ProgressDialog(getActivity());
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);

        return dialog;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

