package biz.coddo.behelpful.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import biz.coddo.behelpful.R;

public class RegistrationStopDialog extends DialogFragment {

    public Dialog onCreateDialog(final Bundle savedInstanceState) {

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setMessage(R.string.registration_stop)
                .setPositiveButton(R.string.ok, null);

        return adb.create();
    }
}
