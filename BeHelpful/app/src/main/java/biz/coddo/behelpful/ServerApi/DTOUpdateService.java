package biz.coddo.behelpful.ServerApi;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import biz.coddo.behelpful.AppLocation.AppLocation;
import biz.coddo.behelpful.AppPreference.DefaultPreference;
import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.DBConnector;
import biz.coddo.behelpful.DTO.ResponseDTO;
import biz.coddo.behelpful.ResponseActivity;

public class DTOUpdateService extends Service {



    private static final String TAG = "DTOUpdateService";
    private static final int AVERAGE_EARTH_RADIUS_IN_KM = 6371;
    private static int userId;
    private static String token;
    private static int visibleArea;
    private static int upgradePeriodInMinute;
    private static int notificationArea;
    public static boolean isPlayServicesAvailable;
    public static AppLocation appLocation;
    private static boolean isMyMarker = false;
    public static volatile boolean isChangeMarkerMap = false;
    private static boolean isRunMarkerUpdateThread = false;
    private static boolean isChangeResponse = false;
    private static volatile HashMap<Integer, MarkerDTO> markerHashMap = new HashMap<Integer, MarkerDTO>();
    private static Thread markerThread, respondThread;
    private static ResponseActivity responseActivityId;

    @Override
    public void onCreate() {
        Log.i(TAG, "OnCreate");
        super.onCreate();
        initPreference();
        appLocation = AppLocation.getAppLocation(getApplicationContext(),isPlayServicesAvailable);
    }

    public void initPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString("userToken", null);
        userId = sharedPreferences.getInt("userId", 0);
        upgradePeriodInMinute = sharedPreferences
                .getInt(DefaultPreference.MARKER_UPGRADE_PERIOD_BACKGROUND_MIN_INT.getStringValue(),
                        DefaultPreference.MARKER_UPGRADE_PERIOD_BACKGROUND_MIN_INT.getIntValue());
        notificationArea = sharedPreferences
                .getInt(DefaultPreference.MARKER_NOTIFICATION_AREA_RADIUS_INT.getName(),
                        DefaultPreference.MARKER_NOTIFICATION_AREA_RADIUS_INT.getIntValue());
        visibleArea = sharedPreferences
                .getInt(DefaultPreference.MARKER_VISIBLE_AREA_SIZE_KM_INT.getName(),
                        DefaultPreference.MARKER_VISIBLE_AREA_SIZE_KM_INT.getIntValue());
        isPlayServicesAvailable = sharedPreferences
                .getBoolean(DefaultPreference.PLAY_SERVICES_AVAILABLE_BOOLEAN.getName(),
                        DefaultPreference.PLAY_SERVICES_AVAILABLE_BOOLEAN.isBooleanValue());
    }

    @Override
    public void onDestroy() {
        isRunMarkerUpdateThread = false;
        try {
            markerThread.join(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        int startRespondThread = 0;
        try {
            startRespondThread = intent.getIntExtra("respondThread", 0);
        } catch (NullPointerException e) {
            Log.e(TAG, "startRespondThread is 0");
        }
        if (!isRunMarkerUpdateThread) {
            Log.i(TAG, "start thread");
            markerThread = new MarkerUpdateThread();
            markerThread.start();
        } else Log.e(TAG, "Thread not start");
        if (startRespondThread == 1) {
            respondThread = new RespondThread();
            respondThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static void setUpgradePeriod(int i) {
        upgradePeriodInMinute = i;
        Log.i(TAG, "upgradePeriod " + i + " min");
    }

    public static boolean isMyMarker() {
        return isMyMarker;
    }

    public static void setResponse(int id) {
        MarkerDTO marker = markerHashMap.get(id);
        marker.respondOnMarker = true;
        markerHashMap.put(id, marker);
    }

    public static void addMyMarker(MarkerDTO marker) {
        markerHashMap.put(marker.userId, marker);
        isMyMarker = true;
        isChangeMarkerMap = true;
    }

    public static void delMyMarker() {
        markerHashMap.remove(userId);
        Log.i(TAG, "delMyMarker");
        isMyMarker = false;
        isChangeMarkerMap = true;
    }

    public static HashMap<Integer, MarkerDTO> getMarkerHashMap() {
        return markerHashMap;
    }

    public static void setResponseActivityId(ResponseActivity responseActivityId) {
        DTOUpdateService.responseActivityId = responseActivityId;
    }

    public static void setIsChangeResponseFalse() {
        isChangeResponse = false;
    }

    public static boolean isChangeResponse() {
        return isChangeResponse;
    }

    class MarkerUpdateThread extends Thread {

        double myLat, myLng;
        double distance;
        HashMap<Integer, MarkerDTO> serverTreeMap;

        @Override
        public void run() {
            Log.i(TAG, "MarkerUpdateThread run");
            isRunMarkerUpdateThread = true;
            Location myLocation;
            while (isRunMarkerUpdateThread) {

                myLocation = appLocation.getMyLocation();
                if (myLocation != null) {
                    //TODO check change geoArea
                    myLat = myLocation.getLatitude();
                    myLng = myLocation.getLongitude();
                    distance = (double) visibleArea / 111.111 / 2;
                    serverTreeMap = null;
                    serverTreeMap = ServerSendData.getAllMarker(token, "" + userId);
                    if (serverTreeMap != null) {
                        deleteMarkerFromMarkerHashMap(serverTreeMap);
                        addMarkerToMarkerHashMap(serverTreeMap);
                        isMyMarker = markerHashMap.containsKey(userId);
                    }
                }
                else Log.i(TAG, "Location is null");
                Log.i(TAG, "MarkerUpdateThread update finish. IsMyMarker " + isMyMarker()
                        + " hashMap size " + markerHashMap.size());
                for (int i = 0; i < upgradePeriodInMinute; i++)
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }

        void deleteMarkerFromMarkerHashMap(HashMap<Integer, MarkerDTO> mHashMap) {
            if (!markerHashMap.isEmpty()) {
                Iterator<Map.Entry<Integer, MarkerDTO>> it = markerHashMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, MarkerDTO> entry = it.next();
                    if (!mHashMap.containsKey(entry.getKey()))
                        it.remove();
                }
            }
        }

        void addMarkerToMarkerHashMap(HashMap<Integer, MarkerDTO> mHashMap) {

            if (!mHashMap.isEmpty()) {
                for (Integer key : mHashMap.keySet()) {
                    if (!markerHashMap.containsKey(key)) {
                        MarkerDTO mMarker = mHashMap.get(key);
                        if (isContainInVisibleArea(mMarker)) {
                            markerHashMap.put(key, mMarker);
                            isChangeMarkerMap = true;
                            if (notificationArea > distanceBetweenTwoPointInKm(mMarker.lat,
                                    mMarker.lng)) ;
                            //TODO write pushMessage
                        }
                    }
                }
            }
        }

        boolean isContainInVisibleArea(MarkerDTO mMarkerDTO) {
            Log.i(TAG, "ContainsVisibleArea: distance " + distance + " lat "
                    + (Math.abs(myLat - mMarkerDTO.lat) < distance) + " lng "
                    + (Math.abs(myLng - mMarkerDTO.lng) < distance * Math.cos(mMarkerDTO.lng)));
            return (Math.abs(myLat - mMarkerDTO.lat) < distance)
                    && (Math.abs(myLng - mMarkerDTO.lng) < distance * Math.cos(mMarkerDTO.lng));

        }

        double distanceBetweenTwoPointInKm(double lat2, double lng2) {
            double mRadianDistance = Math.acos(Math.sin(myLat) * Math.sin(lat2) + Math.cos(myLat)
                    * Math.cos(lat2) * Math.cos(myLng - lng2));
            return mRadianDistance * AVERAGE_EARTH_RADIUS_IN_KM;
        }
    }

    class RespondThread extends Thread {

        ArrayList<ResponseDTO> serverRespondList;
        int lastRespondsIndex = 0;
        int markerId;

        @Override
        public void run() {
            Log.i(TAG, "RespondThread start");
            DBConnector mDBConnector = new DBConnector(getApplicationContext());
            markerId = markerHashMap.get(userId).markerId;
            for (int i = 0; i < 30; i++)
                if (isMyMarker()) {
                    Log.i(TAG, "RespondThread isMyMarker - true, i = " + i);
                    if (i == 29) {
                        boolean isDelMark = ServerSendData.delMark(token, "" + userId, markerId);
                        Log.i(TAG, "RespondThread isDelMark -" + isDelMark + " , i = " + i);
                        if (isDelMark)
                            delMyMarker();
                    }
                    serverRespondList = ServerSendData.getResponses(token, "" + userId, "" + markerId);
                    if (serverRespondList != null) {
                        Log.i(TAG, "serverRespondList size " + serverRespondList.size() + ", lastIndex " + lastRespondsIndex);
                        int size = serverRespondList.size();
                        if (lastRespondsIndex < size) {
                            isChangeResponse = true;
                            for (int m = lastRespondsIndex; m < serverRespondList.size(); m++)
                                mDBConnector.putResponse(serverRespondList.get(m));
                            lastRespondsIndex = serverRespondList.size();
                            if (isChangeResponse && responseActivityId != null)
                                responseActivityId.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        responseActivityId.updateResponseList();
                                        isChangeResponse = false;
                                    }
                                });
                        }
                    }
                    serverRespondList = null;
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else i = 30;

            serverRespondList = ServerSendData.getResponses(token, "" + userId, "" + markerId);
            if (serverRespondList != null)
                for (int m = lastRespondsIndex; m < serverRespondList.size(); m++)
                    mDBConnector.putResponse(serverRespondList.get(m));
            serverRespondList = null;
            Log.i(TAG, "RespondThread stop");
        }
    }
}
