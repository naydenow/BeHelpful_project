package biz.coddo.behelpful.ServerApi;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.DTO.ResponseDTO;

public class ServerSendData {

    private static final String SERVER = "http://dev1.sunny-wsd.com/";
    private static final String TAG = "ServerSendData";

    public static boolean sendRegData(String name, String phone) {

        boolean mSendRegData = false;
        try {
            URL url = new URL(SERVER + "login/login?phone=" + phone + "&name=" + URLEncoder.encode(name, "UTF-8"));

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                JSONObject object;
                try {
                    object = new JSONObject(r.readLine());
                    if (object.getString("responce").equals("true")) mSendRegData = true;

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

            }


        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return mSendRegData;
    }

    public static String[] sendConfirmKey(String key, String phone) {

        String[] mSendRegKey = new String[2];
        try {
            URL url = new URL(SERVER + "login/check?phone=" + phone + "&key=" + key);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                String aString = r.readLine();
                Log.i(TAG, "json " + aString);
                JSONObject object;
                try {
                    object = new JSONObject(aString);
                    if (object.getString("responce").equals("true")) {
                        mSendRegKey[0] = object.getString("id");
                        mSendRegKey[1] = object.getString("token");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return mSendRegKey;
    }

    public static HashMap<Integer, MarkerDTO> getAllMarker(String token, String id){

        HashMap<Integer, MarkerDTO> markerMap = null;
        try {
            URL url = new URL(SERVER + "marker/getAll?token=" + token + "&userid=" + id);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                try {
                    JSONObject jsonObject = new JSONObject(r.readLine());
                    if (jsonObject.getString("responce").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        markerMap = new HashMap<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            int userID = jObject.getInt("user_id");
                            int markerID = jObject.getInt("Id");
                            int typeID = jObject.getInt("type_id");
                            Double lat = jObject.getDouble("lat");
                            Double lng = jObject.getDouble("lon");
                            MarkerDTO marker = new MarkerDTO(userID, markerID, typeID, lat, lng);
                            markerMap.put(markerID, marker);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return markerMap;
    }

    public static int addMark(String token, String id, MarkerDTO markerDTO){

        int MarkerID = 0;
        try {
            URL url = new URL(SERVER + "marker/add?token=" + token + "&userid=" + id + "&typeid="
                    + markerDTO.markerType + "&lat=" + markerDTO.lat + "&lon=" + markerDTO.lng + "&text=");
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                try {
                    JSONObject jsonObject = new JSONObject(r.readLine());
                    if (jsonObject.getString("responce").equals("true")) {
                        MarkerID = jsonObject.getInt("marker_id");
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return MarkerID;
    }

    public static boolean delMark(String token, String id, String markerId){
        boolean isDel = false;
        try {
            URL url = new URL(SERVER + "marker/remove?token=" + token + "&userid=" + id
                    + "&mid=" + markerId);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                JSONObject object;
                try {
                    object = new JSONObject(r.readLine());
                    isDel = object.getString("responce").equals("true");

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

            }


        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return isDel;
    }

    public static boolean sendResponse(String token, String id, String markerId){

        boolean isSend = false;
        try {
            URL url = new URL(SERVER + "response/add?token=" + token + "&userid=" + id
                    + "&markerId=" + markerId + "&comment=");

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                JSONObject object;
                try {
                    object = new JSONObject(r.readLine());
                    if (object.getString("responce").equals("true")) isSend = true;

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

            }


        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return isSend;
    }

    public static ArrayList<ResponseDTO> getResponses(String token, String id, String markerId){
        ArrayList<ResponseDTO> mResponseList = null;

        try {
            URL url = new URL(SERVER + "response/get?token=" + token + "&userid=" + id + "&markerId=" + markerId);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));

                try {
                    JSONObject jsonObject = new JSONObject(r.readLine());
                    if (jsonObject.getString("responce").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        mResponseList = new ArrayList<>(jsonArray.length());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            String userPhone = jObject.getString("phone");
                            String userName = jObject.getString("fio");
                            String data = jObject.getString("time");

                            mResponseList.add(new ResponseDTO(userName, userPhone, data));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return mResponseList;
    }

    public static String getPhone(String token, String id, String markerId){

        String phone = null;
        try {
            URL url = new URL(SERVER + "marker/getPhone?token=" + token + "&userid=" + id
                    + "&markerId=" + markerId);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            int responseCode = httpConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "HTTP connection OK");
                InputStream in = httpConnection.getInputStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                JSONObject object;
                try {
                    object = new JSONObject(r.readLine());
                    if (object.getString("responce").equals("true"))
                        phone = object.getString("result");

                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

            }

        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return phone;
    }
}
