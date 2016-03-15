package biz.coddo.behelpful;

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
import biz.coddo.behelpful.DTO.ResponseDTO;
import biz.coddo.behelpful.Notification.MarkerNotification;
import biz.coddo.behelpful.Notification.ResponseNotification;
import biz.coddo.behelpful.ServerApi.ServerSendData;

public class DTOUpdateService extends Service {

    private static final String TAG = "DTOUpdateService";
    private static final int AVERAGE_EARTH_RADIUS_IN_KM = 6371;
    private String token;
    private int userId;
    private static volatile int upgradePeriodInMinute;

    private static volatile int myMarkerID;

    private static SharedPreferences sharedPreferences;
    public static AppLocation appLocation;
    private static volatile boolean isMyMarker = false;
    public static volatile boolean isChangeMarkerMap = false;
    private static volatile boolean isRunMarkerUpdateThread = false;
    private static volatile boolean isChangeResponse = false;
    private static volatile HashMap<Integer, MarkerDTO> markerHashMap;
    private Thread markerThread;
    public static volatile int newMarkerNotifyCount = 0;
    public static volatile int newResponseNotifyCount = 0;
    @Override
    public void onCreate() {
        Log.i(TAG, "OnCreate");
        super.onCreate();
        initPreference();
        initAppLocation();
        markerHashMap = new HashMap<>();
    }

    private void initAppLocation(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPlayServicesAvailable = sharedPreferences
                .getBoolean(DefaultPreference.PLAY_SERVICES_AVAILABLE_BOOLEAN.getName(),
                        DefaultPreference.PLAY_SERVICES_AVAILABLE_BOOLEAN.isBooleanValue());
        appLocation = AppLocation.getAppLocation(getApplicationContext(),isPlayServicesAvailable);
    }

    private void initPreference() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        token = sharedPreferences.getString("userToken", null);
        userId = sharedPreferences.getInt("userId", 0);
        upgradePeriodInMinute = sharedPreferences
                .getInt(DefaultPreference.MARKER_UPGRADE_PERIOD_BACKGROUND_MIN_INT.getStringValue(),
                        DefaultPreference.MARKER_UPGRADE_PERIOD_BACKGROUND_MIN_INT.getIntValue());
        myMarkerID = sharedPreferences.getInt(DefaultPreference.MY_MARKER_ID_INT.getName(),
                DefaultPreference.MY_MARKER_ID_INT.getIntValue());
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
            isRunMarkerUpdateThread =true;
            markerThread = new MarkerUpdateThread();
            markerThread.start();
            isRunMarkerUpdateThread = markerThread.isAlive();
        } else Log.e(TAG, "Thread not start");
        if (startRespondThread == 1) {
             Thread respondThread = new RespondThread();
            respondThread.start();
        }
        return START_STICKY;
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
        markerHashMap.put(marker.markerId, marker);
        isMyMarker = true;
        isChangeMarkerMap = true;
    }

    public static void delMyMarker() {
        markerHashMap.remove(myMarkerID);
        Log.i(TAG, "delMyMarker");
        isMyMarker = false;
        isChangeMarkerMap = true;
        sharedPreferences.edit().remove(DefaultPreference.MY_MARKER_ID_INT.getName()).commit();
        myMarkerID = 0;
    }

    public static HashMap<Integer, MarkerDTO> getMarkerHashMap() {
        return markerHashMap;
    }

    public static void setIsChangeResponseFalse() {
        isChangeResponse = false;
    }

    public static boolean isChangeResponse() {
        return isChangeResponse;
    }

    public static int getMyMarkerID() {
        return myMarkerID;
    }

    public static void setMyMarkerID(int myMarkerID) {
        DTOUpdateService.myMarkerID = myMarkerID;
    }

    class MarkerUpdateThread extends Thread {

        double myLat, myLng;
        double distance;
        HashMap<Integer, MarkerDTO> serverTreeMap;

        @Override
        public void run() {
            Log.i(TAG, "MarkerUpdateThread run");
            Location myLocation;
            while (isRunMarkerUpdateThread) {

                int notificationArea = sharedPreferences
                        .getInt(DefaultPreference.MARKER_NOTIFICATION_AREA_RADIUS_INT.getName(),
                                DefaultPreference.MARKER_NOTIFICATION_AREA_RADIUS_INT.getIntValue());
                int visibleArea = sharedPreferences
                        .getInt(DefaultPreference.MARKER_VISIBLE_AREA_SIZE_KM_INT.getName(),
                                DefaultPreference.MARKER_VISIBLE_AREA_SIZE_KM_INT.getIntValue());
                myMarkerID = sharedPreferences
                        .getInt(DefaultPreference.MY_MARKER_ID_INT.getName(),
                                DefaultPreference.MY_MARKER_ID_INT.getIntValue());
                getMyMarkerID();
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
                        if (addMarkerToMarkerHashMap(serverTreeMap, notificationArea))
                            isChangeMarkerMap = true;
                        isMyMarker = markerHashMap.containsKey(myMarkerID);
                    }
                } else Log.i(TAG, "Location is null");
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

        boolean addMarkerToMarkerHashMap(HashMap<Integer, MarkerDTO> mHashMap, int notificationArea) {

            boolean isChange = false;

            if (!mHashMap.isEmpty()) {
                for (Integer key : mHashMap.keySet()) {
                    if (!markerHashMap.containsKey(key)) {
                        MarkerDTO mMarker = mHashMap.get(key);
                        if (isContainInVisibleArea(mMarker)) {
                            markerHashMap.put(key, mMarker);
                            isChange = true;
                            if (notificationArea > distanceBetweenTwoPointInKm(mMarker.lat, mMarker.lng) &&
                                    key != myMarkerID) {
                                newMarkerNotifyCount++;
                                if (newMarkerNotifyCount == 1)
                                    new MarkerNotification(getApplicationContext(), key);
                                else new MarkerNotification(getApplicationContext(), 0);
                            }
                        }
                    }
                }
            }
            return isChange;
        }

        boolean isContainInVisibleArea(MarkerDTO mMarkerDTO) {
            Log.i(TAG, "ContainsVisibleArea: distance " + distance + " lat "
                    + (Math.abs(myLat - mMarkerDTO.lat) < distance) + " lng "
                    + (Math.abs(myLng - mMarkerDTO.lng) < distance * Math.cos(mMarkerDTO.lng)));
            //return (Math.abs(myLat - mMarkerDTO.lat) < distance)
            //        && (Math.abs(myLng - mMarkerDTO.lng) < distance * Math.cos(mMarkerDTO.lng));
            //TODO change after a-test
            return true;

        }

        double distanceBetweenTwoPointInKm(double lat2, double lng2) {
            double mRadianDistance = 2 * AVERAGE_EARTH_RADIUS_IN_KM * Math.asin(
                    Math.sqrt(Math.pow(Math.sin((lat2 - myLat) * Math.PI / 180 / 2), 2)
                                    + Math.cos(myLat * Math.PI / 180 / 2)
                                    * Math.cos(lat2 * Math.PI / 180 / 2)
                                    * Math.pow(Math.sin((lng2 - myLng) * Math.PI / 180 / 2) , 2)));
            Log.i(TAG, "" + mRadianDistance);
            return mRadianDistance;
        }
    }

    class RespondThread extends Thread {

        ArrayList<ResponseDTO> serverRespondList;
        int lastRespondsIndex = 0;

        @Override
        public void run() {
            Log.i(TAG, "RespondThread start");
            DBConnector mDBConnector = new DBConnector(getApplicationContext());
            getMyMarkerID();
            for (int i = 0; i < 30; i++)
                if (isMyMarker()) {
                    Log.i(TAG, "RespondThread isMyMarker - true, i = " + i);
                    if (i == 29) {
                        boolean isDelMark = ServerSendData.delMark(token, "" + userId, "" + myMarkerID);
                        Log.i(TAG, "RespondThread isDelMark -" + isDelMark + " , i = " + i);
                        if (isDelMark)
                            delMyMarker();
                    }
                    serverRespondList = ServerSendData.getResponses(token, "" + userId, "" + myMarkerID);
                    if (serverRespondList != null) {
                        Log.i(TAG, "serverRespondList size " + serverRespondList.size() + ", lastIndex " + lastRespondsIndex);
                        int size = serverRespondList.size();
                        if (lastRespondsIndex < size) {
                            isChangeResponse = true;
                            for (int m = lastRespondsIndex; m < serverRespondList.size(); m++) {
                                mDBConnector.putResponse(serverRespondList.get(m));
                                newResponseNotifyCount++;
                                new ResponseNotification(getApplicationContext());
                            }
                            lastRespondsIndex = serverRespondList.size();
                        }
                    }
                    serverRespondList = null;
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else i = 30;

            serverRespondList = ServerSendData.getResponses(token, "" + userId, "" + myMarkerID);
            if (serverRespondList != null)
                for (int m = lastRespondsIndex; m < serverRespondList.size(); m++)
                    mDBConnector.putResponse(serverRespondList.get(m));
            serverRespondList = null;
            Log.i(TAG, "RespondThread stop");
        }
    }
}
