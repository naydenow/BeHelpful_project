package biz.coddo.behelpful.AppMap;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import biz.coddo.behelpful.DTO.MarkerDTO;
import biz.coddo.behelpful.MainActivity;
import biz.coddo.behelpful.R;
import biz.coddo.behelpful.ServerApi.MarkerUpdateService;

public class AppMapGoogleMap extends AppMap implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static Map<Integer, Marker> googleMapMarkerMap = new HashMap<>();
    private final String TAG = "AppMapGoogleMap";
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View fragmentView = inflater.inflate(R.layout.content_main, container, false);
        Log.i(TAG, "mapFragment " + mapFragment);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        Log.i(TAG, "after init mapFragment " + mapFragment);
        if (mapFragment == null) Log.e(TAG, "mapFragment is null");
        else mapFragment.getMapAsync(this);

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mapFragment != null) mapFragment.onAttach(context);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        setCameraOnMyLocation();
        if (!MarkerUpdateService.getMarkerHashMap().isEmpty() && mMap != null)
            for (MarkerDTO marker : MarkerUpdateService.getMarkerHashMap().values()) {
                addMark(marker);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        addAllMarker();
    }

    public void setCameraOnMyLocation() {
        Location location = MainActivity.appLocation.getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    public void removeMark(int userId) {
        if (googleMapMarkerMap.containsKey(userId)) {
            googleMapMarkerMap.get(userId).remove();
            googleMapMarkerMap.remove(userId);
        } else Log.e(TAG, "GoogleMarker id = " + userId + " doesn't exist at googleMapMarkerId");
    }

    public void addMark(MarkerDTO marker) {
        int key = marker.userId;
        if (!googleMapMarkerMap.containsKey(key)) {
            Marker mMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(marker.lat, marker.lng))
                    .anchor((float) 0.5, (float) 0.5)
                    .icon(BitmapDescriptorFactory.fromResource(setIconByID(marker.markerType)))
                    .snippet("" + key)
                    .visible(true));
            googleMapMarkerMap.put(marker.userId, mMarker);
        } else googleMapMarkerMap.get(key).setVisible(true);
    }

    @Override
    public void addAllMarker() {
        HashMap<Integer, MarkerDTO> hashMap = MarkerUpdateService.getMarkerHashMap();
        if (mMap != null) {
            if (!googleMapMarkerMap.isEmpty()) {
                Iterator<Map.Entry<Integer, Marker>> it = googleMapMarkerMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, Marker> entry = it.next();
                    if (!hashMap.containsKey(entry.getKey())) {
                        entry.getValue().remove();
                        it.remove();
                    }
                }
            }
            for (MarkerDTO marker : MarkerUpdateService.getMarkerHashMap().values())
                addMark(marker);
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        markerClickListener.onMarkerClick(Integer.parseInt(marker.getSnippet()));
        Log.i(TAG, "googleMap onMarkerClick");
        return false;
    }
}
