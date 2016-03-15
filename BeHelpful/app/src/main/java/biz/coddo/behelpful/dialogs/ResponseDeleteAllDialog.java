package biz.coddo.behelpful.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import biz.coddo.behelpful.R;
import biz.coddo.behelpful.ResponseActivity;

public class ResponseDeleteAllDialog extends DialogFragment {

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        final ResponseActivity activity = (ResponseActivity) getActivity();

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage(R.string.delete_all_response)
                .setNeutralButton(R.string.CANCEL, null)
                .setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.deleteAllResponse();
                    }
                });

        return adb.create();
    }
}
