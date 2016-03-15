package biz.coddo.behelpful.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.MainActivity;
import biz.coddo.behelpful.R;
import biz.coddo.behelpful.DTOUpdateService;
import biz.coddo.behelpful.ServerApi.ServerAsyncTask;

public class MarkerClickDialog extends DialogFragment {

    private static final String TAG = "MarkerClickDialog";
    private int userId;
    MarkerDTO marker;
    private String token;

    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog");
        Bundle mArgs = getArguments();
        int id = mArgs.getInt("id");
        userId = mArgs.getInt("userId");
        token = mArgs.getString("token");

        Log.i(TAG, "For markerID " + id);

        marker = DTOUpdateService.getMarkerHashMap().get(id);

        int title = setTitleById(marker);

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        if (title == 0) {
            adb.setTitle(R.string.your_marker)
                    .setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity activity = (MainActivity) getActivity();
                            activity.fabDeleteMark();
                        }
                    })
                    .setNegativeButton(R.string.CANCEL, null)
                    .setMessage(R.string.delete_marker);

        } else if (title == R.string.fab_menu_id5_ReadyToHelp) {
            adb.setTitle(title)
                    .setPositiveButton(R.string.CALL, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AsyncTask<String, Void, String> getPhone = new ServerAsyncTask.TaskGetPhone();
                            getPhone.execute(token, "" + userId, "" + marker.markerId);
                            String phone = "";
                            try {
                                phone = getPhone.get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if (phone.isEmpty()) Log.e(TAG, "Phone is empty");
                            else {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:+" + phone));
                                startActivity(intent);
                            }
                        }
                    })
                    .setNegativeButton(R.string.CANCEL, null)
                    .setMessage(R.string.call_user);
        } else {
            adb.setTitle(title)
                    .setNegativeButton(R.string.route, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("google.navigation:q=" + marker.lat + "," + marker.lng));
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton(R.string.CANCEL, null)
                    .setMessage(R.string.choose_action);
            if (!marker.respondOnMarker)
                adb.setPositiveButton(R.string.response, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AsyncTask<String, Void, Boolean> sendResponse = new ServerAsyncTask.TaskSendResponse();
                        sendResponse.execute(token, "" + userId, "" + marker.markerId);
                    }
                });
        }
        return adb.create();
    }

    int setTitleById(MarkerDTO mMarker) {
        int title = 0;
        if (mMarker.userId == userId) title = 0;
        else switch (mMarker.markerType) {
            case 1:
                title = R.string.fab_menu_id1_Emergency;
                break;
            case 2:
                title = R.string.fab_menu_id2_Accident;
                break;
            case 3:
                title = R.string.fab_menu_id3_Evacuation;
                break;
            case 4:
                title = R.string.fab_menu_id4_Repair;
                break;
            case 5:
                title = R.string.fab_menu_id5_ReadyToHelp;
                break;
        }
        return title;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.d(TAG, "Dialog 2: onDismiss");
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Log.d(TAG, "Dialog 2: onCancel");
    }

}
