package org.mavadvise.commons;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by SaiKumar on 4/15/2017.
 */

public class AlertDialogHelper extends DialogFragment {
    private String msg = "";
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;

    public static AlertDialogHelper newInstance(String msg,
                                                DialogInterface.OnClickListener positiveListener,
                                                DialogInterface.OnClickListener negativeListener) {
        AlertDialogHelper instance = new AlertDialogHelper();
        instance.msg = msg;
        instance.positiveListener = positiveListener;
        instance.negativeListener = negativeListener;

        return instance;
    }

    public static AlertDialogHelper newInstance(String msg,
                                                DialogInterface.OnClickListener positiveListener) {
        AlertDialogHelper instance = new AlertDialogHelper();
        instance.msg = msg;

        instance.positiveListener = positiveListener;
        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (negativeListener == null)
            return new AlertDialog.Builder(getActivity())
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Ok", positiveListener).create();
        else
            return new AlertDialog.Builder(getActivity())
                    .setMessage(msg)
                    .setCancelable(false)
                    .setPositiveButton("Ok", positiveListener)
                    .setNegativeButton("Cancel", negativeListener).create();
    }
}
