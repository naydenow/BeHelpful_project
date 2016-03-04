package biz.coddo.behelpful.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;

import biz.coddo.behelpful.R;

public class GMapNotInstalledDialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "GMapNotInstalledDialog";

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.AppDialog_GoogleMap_title)
                .setMessage(R.string.AppDialog_GoogleMap_none)
                .setPositiveButton(R.string.ok, this);
        return adb.create();
    }

    public void onClick(DialogInterface dialog, int which) {

    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(LOG_TAG, "Dialog 2: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(LOG_TAG, "Dialog 2: onCancel");
    }
}