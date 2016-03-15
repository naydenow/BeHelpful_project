package biz.coddo.behelpful.AppMap;


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
import biz.coddo.behelpful.R;
import biz.coddo.behelpful.DTOUpdateService;

public class AppMapGoogleMap extends AppMap implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private Map<Integer, Marker> googleMapMarkerMap = new HashMap<>();
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
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (key == 0) setCameraOnMyLocation();
        else setCameraOnMarker(key);
        key = 0;
        if (!DTOUpdateService.getMarkerHashMap().isEmpty() && mMap != null)
            for (MarkerDTO marker : DTOUpdateService.getMarkerHashMap().values()) {
                addMark(marker);
            }
    }

    @Override
    public void onResume() {
        super.onResume();
        addAllMarker();
    }

    public void setCameraOnMyLocation() {
        if (mMap != null) {
            Location location = DTOUpdateService.appLocation.getMyLocation();
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            } else Log.i(TAG, "Location is null");
        }
    }

    public void removeMark(int key) {
        if (googleMapMarkerMap.containsKey(key)) {
            googleMapMarkerMap.get(key).remove();
            googleMapMarkerMap.remove(key);
        } else Log.e(TAG, "GoogleMarker id = " + key + " doesn't exist at googleMapMarkerId");
    }

    public void addMark(MarkerDTO marker) {
        Log.i(TAG, "addMark");
        int key = marker.markerId;
        if (!googleMapMarkerMap.containsKey(key)) {
            Marker mMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(marker.lat, marker.lng))
                    .anchor((float) 1, (float) 0.5)
                    .icon(BitmapDescriptorFactory.fromResource(setIconByID(marker.markerType)))
                    .snippet("" + key)
                    .visible(true));
            googleMapMarkerMap.put(marker.markerId, mMarker);
        } else googleMapMarkerMap.get(key).setVisible(true);
    }

    @Override
    public void addAllMarker() {
        HashMap<Integer, MarkerDTO> hashMap = DTOUpdateService.getMarkerHashMap();
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
            for (MarkerDTO marker : DTOUpdateService.getMarkerHashMap().values())
                addMark(marker);
        }
    }

    @Override
    public void setCameraOnMarker(int key) {
        if (mMap != null) {
            MarkerDTO marker = DTOUpdateService.getMarkerHashMap().get(key);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.lat, marker.lng), 16));
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        markerClickListener.onMarkerClick(Integer.parseInt(marker.getSnippet()));
        Log.i(TAG, "googleMap onMarkerClick");
        return false;
    }
}
