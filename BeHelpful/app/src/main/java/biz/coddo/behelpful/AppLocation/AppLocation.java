package biz.coddo.behelpful.AppLocation;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public abstract class AppLocation {

    private static final String TAG = "AppLocation";
    private LocationManager locationManager;

    public static AppLocation getAppLocation(Context context, boolean isPlayServiceAvailable){
        AppLocation appLocation = null;
        Log.i(TAG, "isPlayServicesAvailable" + isPlayServiceAvailable);
        if (isPlayServiceAvailable) appLocation = new PlayServicesLocation(context);
        else appLocation = new BaseAndroidLocation(context);
        return appLocation;
    }

    public abstract Location getMyLocation();
}
