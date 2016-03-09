package biz.coddo.behelpful.AppLocation;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class BaseAndroidLocation extends AppLocation{
    private LocationManager locationManager;
    private String provider;

    public BaseAndroidLocation(Context context) {
        // Get LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        provider = locationManager.getBestProvider(criteria, true);
    }

    public Location getMyLocation() {
        // Get Current Location
        return locationManager.getLastKnownLocation(provider);

    }
}
