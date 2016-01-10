package biz.coddo.behelpful;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class AppMap extends Fragment implements OnMapReadyCallback {

    Marker myMarker;

    private final String TAG = "AppMap";

    private GoogleMap mMap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.content_main, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) Log.e(TAG, "mapFragment is null");
        else mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            setCameraOnMyLocation();
            myMarker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(0, 0))
                            .visible(false)
            );

    }

    void setCameraOnMyLocation() {
        Location location = MainActivity.appLocation.getMyLocation();

        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    void removeMark(Marker marker) {
        marker.remove();
    }

    Marker addMark(Marker marker, int id) {
        if (marker.isVisible()) removeMark(marker);
        Location location = MainActivity.appLocation.getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .anchor((float) 0.5, (float) 0.5)
                        .icon(BitmapDescriptorFactory.fromResource(setIconByID(id)))
                        .visible(true)
        );
        return marker;
    }

    int setIconByID(int id) {
        int idIcon = getResources().getIdentifier("ic_emergency", "mipmap", MainActivity.PACKAGE_NAME);
        switch (id) {
            case 1:
                idIcon = getResources().getIdentifier("ic_emergency", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 2:
                idIcon = getResources().getIdentifier("ic_accident", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 3:
                idIcon = getResources().getIdentifier("ic_evacuation", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 4:
                idIcon = getResources().getIdentifier("ic_repair", "mipmap", MainActivity.PACKAGE_NAME);
                break;
            case 5:
                idIcon = getResources().getIdentifier("ic_ready_to_help", "mipmap", MainActivity.PACKAGE_NAME);
                break;
        }
        return idIcon;
    }
}
