package biz.coddo.behelpful.ServerApi;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.DTOUpdateService;

public class ServerAsyncTask {

    private static final String TAG = "ServerAsyncTask";

    //params[0] - name, params[1] - phone
    public static class SendRegistrationData extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            return ServerSendData.sendRegData(params[0], params[1]);
        }
    }

    //params[0] - key, params[1] - phone
    public static class SendConfirmKey extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            return ServerSendData.sendConfirmKey(params[0], params[1]);
        }
    }

    //params[0] - token, params[1] - userId, params[2] - markerTypeId
    public static class TaskAddMarker extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            Log.i(TAG, "addMark");
            Location location = DTOUpdateService.appLocation.getMyLocation();
            int id = 0;
            if (location != null) {
                MarkerDTO marker = new MarkerDTO(Integer.parseInt(params[1]), 0, Integer.parseInt(params[2]),
                        location.getLatitude(), location.getLongitude());
                id = ServerSendData.addMark(params[0], params[1], marker);
                Log.i(TAG, "MarkerId " + id);
                if (id > 0) {
                    marker.markerId = id;
                    DTOUpdateService.addMyMarker(marker);
                } else {

                    Log.i(TAG, "Error to add marker on server");
                }
            }
            else Log.i(TAG, "Location is null");
            return id;
        }
    }

    //params[0] - token, params[1] - userId
    public static class TaskDelMarker extends AsyncTask<String, Void, Boolean> {


        @Override
        protected Boolean doInBackground(String... params) {
            Boolean mBoolean = false;
            Log.i(TAG, "DelMarker");
            Log.i(TAG, "DelMark userId " + params[0] + " token " + params[1] + " markerId " + params[2]);
            if (ServerSendData.delMark(params[0], params[1], params[2])) {
                DTOUpdateService.delMyMarker();
                mBoolean = true;
            } else {
                Log.e(TAG, "Error to delete marker from server");
            }
            return mBoolean;
        }
    }

    //params[0] - token, params[1] - userId, params[2] - markerGetPhoneId
    public static class TaskGetPhone extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "GetPhone");
            String phone = ServerSendData.getPhone(params[0], params[1], params[2]);
            if (phone.isEmpty()) Log.e(TAG, "Error to getPhone from server");
            DTOUpdateService.delMyMarker();
            return phone;
        }
    }

    //params[0] - token, params[1] - userId, params[2] - responseMarkerId
    public static class TaskSendResponse extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Log.i(TAG, "SendResponse");
            if (ServerSendData.sendResponse(params[0], params[1], "" + params[2])) {
                DTOUpdateService.setResponse(Integer.parseInt(params[2]));
                return true;
            } else return false;
        }
    }
}
