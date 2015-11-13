package biz.coddo.behelpful;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Toolbar toolbar;
    private GoogleMap mMap;
    private RelativeLayout fabMenu;
    private Marker myMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        //Initialez map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Initialez myLocButton
        FloatingActionButton myLocButton = (FloatingActionButton) findViewById(R.id.myLocButton);
        myLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCameraOnMyLocation();
            }
        });

        //Initialez fabMenu
        fabMenu = (RelativeLayout) findViewById(R.id.fab_menu);
        View.OnClickListener fabMenuButtonsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.addMarkId1:
                        myMarker = addMyMark(myMarker, 1);
                        break;
                    case R.id.addMarkId2:
                        myMarker = addMyMark(myMarker, 2);
                        break;
                    case R.id.addMarkId3:
                        myMarker = addMyMark(myMarker, 3);
                        break;
                    case R.id.addMarkId4:
                        myMarker = addMyMark(myMarker, 4);
                        break;
                    case R.id.addMarkId5:
                        myMarker = addMyMark(myMarker, 5);
                        break;
                }
                fabMenu.setVisibility(view.INVISIBLE);
            }
        };
        FloatingActionButton addMarkId1 = (FloatingActionButton) findViewById(R.id.addMarkId1);
        addMarkId1.setOnClickListener(fabMenuButtonsOnClickListener);
        FloatingActionButton addMarkId2 = (FloatingActionButton) findViewById(R.id.addMarkId2);
        addMarkId2.setOnClickListener(fabMenuButtonsOnClickListener);
        FloatingActionButton addMarkId3 = (FloatingActionButton) findViewById(R.id.addMarkId3);
        addMarkId3.setOnClickListener(fabMenuButtonsOnClickListener);
        FloatingActionButton addMarkId4 = (FloatingActionButton) findViewById(R.id.addMarkId4);
        addMarkId4.setOnClickListener(fabMenuButtonsOnClickListener);
        FloatingActionButton addMarkId5 = (FloatingActionButton) findViewById(R.id.addMarkId5);
        addMarkId5.setOnClickListener(fabMenuButtonsOnClickListener);






        //Initialez fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fabMenu.setVisibility(view.VISIBLE);
                fabMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fabMenu.setVisibility(view.INVISIBLE);
                    }
                });
            }
        });


    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        setCameraOnMyLocation();
        myMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .visible(false)
        );
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private Location getMyLocation() {
        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        return myLocation;
    }

    private void setCameraOnMyLocation() {
        Location location = getMyLocation();
        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // Show the current location in Google Map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    private int setIconByID (int id){
        int idIcon = getResources().getIdentifier("ic_emergency" , "mipmap", getPackageName());;
        switch (id) {
            case 1:
                idIcon = getResources().getIdentifier("ic_emergency" , "mipmap", getPackageName());
                break;
            case 2:
                idIcon = getResources().getIdentifier("ic_accident" , "mipmap", getPackageName());
                break;
            case 3:
                idIcon = getResources().getIdentifier("ic_evacuation" , "mipmap", getPackageName());
                break;
            case 4:
                idIcon = getResources().getIdentifier("ic_repair" , "mipmap", getPackageName());
                break;
            case 5:
                idIcon = getResources().getIdentifier("ic_ready_to_help" , "mipmap", getPackageName());
                break;
        }
        return idIcon;
    }

    private void removeMyMark(Marker marker) {
        marker.remove();
    }

    private Marker addMyMark(Marker marker, int id) {
        if (marker.isVisible()) removeMyMark(marker);
        Location location = getMyLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .anchor((float) 0.5, (float) 0.5)
                .icon(BitmapDescriptorFactory.fromResource(setIconByID(id)))
                .visible(true)
        );
        return marker;
    }

    @Override
    public void onBackPressed() {
        if (fabMenu.getVisibility() == View.VISIBLE)
            fabMenu.setVisibility(View.INVISIBLE);
        else super.onBackPressed();
    }
}
