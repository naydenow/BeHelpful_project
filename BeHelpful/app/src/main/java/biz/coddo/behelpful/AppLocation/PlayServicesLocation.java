package biz.coddo.behelpful.AppLocation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;

public class PlayServicesLocation extends AppLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "PlayServicesLocation";
    private GoogleApiClient mGoogleApiClient;

    public PlayServicesLocation (Context context){
        Log.i(TAG, "Constructor");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "OnConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "OnConnectedSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public Location getMyLocation() {
        if (mGoogleApiClient == null) Log.i(TAG, "GoogleAPIClient null");
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
}
