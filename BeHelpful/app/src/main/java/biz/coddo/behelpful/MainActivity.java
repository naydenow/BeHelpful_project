package biz.coddo.behelpful;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RelativeLayout fabMenu;
    private final String TAG = "main";
    static String PACKAGE_NAME;
    static AppLocation appLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        initToolbar();

//        boolean firstTimeLoadindApp = sharedPreferences.getBoolean("firstTimeLoadindApp", true);
//        if (firstTimeLoadindApp) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("firstTimeLoadindApp", false);
//            editor.putString("appID",  UUID.randomUUID().toString());
//            editor.apply();
//        }


        appLocation = new AppLocation(this);

        //Initialez map fragment
        Log.i(TAG, "Create new AppMap");
        final AppMap appMap = new AppMap();
        Log.i(TAG, "Create FragmentTransaction");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Log.i(TAG, "Create add");
        ft.add(R.id.content_main, appMap);
        Log.i(TAG, "Create commit");
        ft.commit();


        //Initialez myLocButton
        FloatingActionButton myLocButton = (FloatingActionButton) findViewById(R.id.myLocButton);
        myLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appMap.setCameraOnMyLocation();
            }
        });

        //Initialez fabMenu
        fabMenu = (RelativeLayout) findViewById(R.id.fab_menu);
        View.OnClickListener fabMenuButtonsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.addMarkId1:
                        appMap.myMarker = appMap.addMark(appMap.myMarker, 1);
                        break;
                    case R.id.addMarkId2:
                        appMap.myMarker = appMap.addMark(appMap.myMarker, 2);
                        break;
                    case R.id.addMarkId3:
                        appMap.myMarker = appMap.addMark(appMap.myMarker, 3);
                        break;
                    case R.id.addMarkId4:
                        appMap.myMarker = appMap.addMark(appMap.myMarker, 4);
                        break;
                    case R.id.addMarkId5:
                        appMap.myMarker = appMap.addMark(appMap.myMarker, 5);
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
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        if (fabMenu.getVisibility() == View.VISIBLE)
            fabMenu.setVisibility(View.INVISIBLE);
        else super.onBackPressed();
    }
}
