package biz.coddo.behelpful.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import biz.coddo.behelpful.DTO.ResponseDTO;
import biz.coddo.behelpful.R;
import biz.coddo.behelpful.ResponseActivity;

public class ResponseClickDialog extends DialogFragment {

    private static final String TAG = "ResponseClickDialog";
    ResponseDTO response;
    int id;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        id = mArgs.getInt("id");

        final ResponseActivity activity = (ResponseActivity) getActivity();
        this.response = activity.getAdapter().getResponseArrayList().get(id);

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage(R.string.choose_action)
                .setNegativeButton(R.string.DELETE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Delete");
                        activity.deleteResponseById(id);
                    }
                })
                .setNeutralButton(R.string.CANCEL, null)
                .setPositiveButton(R.string.CALL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "Call user");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("tel:+" + response.getUserPhone()));
                        startActivity(intent);
                    }
                });

        return adb.create();
    }
}
