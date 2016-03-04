package biz.coddo.behelpful.ServerApi;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.MainActivity;

public class ServerAsyncTask {

    private static final String TAG = "ServerAsyncTask";

    //params[0] - token, params[1] - userId, params[2] - markerTypeId
    public static class TaskAddMarker extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            boolean isOk = false;
            Log.i(TAG, "addMark");
            Location location = MainActivity.appLocation.getMyLocation();
            MarkerDTO marker = new MarkerDTO(Integer.parseInt(params[1]), 0, Integer.parseInt(params[2]),
                    location.getLatitude(), location.getLongitude());
            int id = ServerSendData.addMark(params[0], params[1], marker);
            Log.i(TAG, "MarkerkId" + id);
            if (id > 0) {
                marker.markerId = id;
                MarkerUpdateService.addMyMarker(marker);
                isOk = true;
            } else {

                Log.i(TAG, "Error to add marker on server");
            }
            return isOk;

        }
    }

    //params[0] - token, params[1] - userId
    public static class TaskDelMarker extends AsyncTask<String, Void, Void> {


        @Override
        protected Void doInBackground(String... params) {
            Log.i(TAG, "DelMarker");
            int i = MarkerUpdateService.getMarkerHashMap().get(Integer.parseInt(params[1])).markerId;
            Log.i(TAG, "DelMark userId " + params[0] + " token " + params[1] + " markerId " + i);
            if (ServerSendData.delMark(params[0], params[1], i)) {
                MarkerUpdateService.delMyMarker();
            } else {
                Log.e(TAG, "Error to delete marker from server");
            }
            return null;
        }
    }

    //params[0] - token, params[1] - userId, params[2] - markerGetPhoneId
    public static class TaskGetPhone extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "GetPhone");
            String phone = ServerSendData.getPhone(params[0], params[1], params[2]);
            if (phone.isEmpty()) Log.e(TAG, "Error to getPhone from server");
            MarkerUpdateService.delMyMarker();
            return phone;
        }
    }

    //params[0] - token, params[1] - userId, params[2] - responseMarkerUserId
    public static class TaskSendResponse extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Log.i(TAG, "GetPhone");
            int i = Integer.parseInt(params[2]);
            MarkerDTO marker = MarkerUpdateService.getMarkerHashMap().get(i);
            if (ServerSendData.sendResponse(params[0], params[1], "" + marker.markerId)) {
                MarkerUpdateService.setResponse(i);
                return true;
            } else return false;
        }
    }
}
