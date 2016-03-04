package biz.coddo.behelpful;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.support.v4.app.FragmentTransaction;

import java.util.concurrent.ExecutionException;

import biz.coddo.behelpful.AppMap.AppMap;
import biz.coddo.behelpful.AppMap.AppMapGoogleMap;
import biz.coddo.behelpful.ServerApi.MarkerUpdateService;
import biz.coddo.behelpful.ServerApi.ServerAsyncTask;
import biz.coddo.behelpful.dialogs.MarkerClickDialog;

public class MainActivity extends AppCompatActivity implements AppMap.onMarkerClickListener {

    private static final String TAG = "MainActivity";
    public static String PACKAGE_NAME;
    public static int userId;
    private static String token;
    public static AppLocation appLocation;
    private AppMap appMap;
    private RelativeLayout fabMenu;
    private Animation rotate_forward, rotate_backward;
    private boolean isOpenFabMenu = false;
    private boolean isAddFab = true;
    private boolean isNewResponse = false;
    private FloatingActionButton fab;
    private FloatingActionButton myLocButton;
    private ChangeListener changeListener;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        token = intent.getStringExtra("userToken");
        Log.i(TAG, token);
        userId = intent.getIntExtra("userId", 0);
        Log.i(TAG, "" + userId);

        PACKAGE_NAME = getApplicationContext().getPackageName();

        appLocation = new AppLocation(this);

        initToolbar();

        //Initialez map fragment
        initGoogleMapFragment();


        //Initialez myLocButton
        myLocButton = (FloatingActionButton) findViewById(R.id.myLocButton);
        myLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appMap.setCameraOnMyLocation();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        MarkerUpdateService.setUpgradePeriod(3);
        changeListener.stopSelf();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MarkerUpdateService.setUpgradePeriod(1);

        changeListener = new ChangeListener(this);
        changeListener.start();

        //Initialez fabMenu
        initFabMenu();

        //Initialez fab
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_rotate_backward);
        initFab();

    }

    @Override
    public void onBackPressed() {
        if (isOpenFabMenu) {
            closeFabMenu();
        } else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MarkerUpdateService.setUpgradePeriod(15);
    }

    private void initGoogleMapFragment() {
        appMap = new AppMapGoogleMap();
        Log.i(TAG, "Fragment Transaction");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content_main, appMap);
        ft.commit();

    }

    private void initFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (MarkerUpdateService.isMyMarker()) {
            Log.i(TAG, "isMyMarker true");
            if (isAddFab) fab.startAnimation(rotate_forward);
            fab.setOnClickListener(fabDeleteMark());
            isAddFab = false;
        } else {
            fab.setOnClickListener(fabAddMark());
            if (!isAddFab) fab.startAnimation(rotate_backward);
        }

    }

    private View.OnClickListener fabDeleteMark() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fab.startAnimation(rotate_backward);
                AsyncTask<String, Void, Void> delMarker = new ServerAsyncTask.TaskDelMarker();
                delMarker.execute(token, "" + userId);
            }
        };
    }

    private View.OnClickListener fabAddMark() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFabMenu();
                fabMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeFabMenu();
                    }
                });
            }
        };
    }

    private void initFabMenu() {
        fabMenu = (RelativeLayout) findViewById(R.id.fab_menu);
        View.OnClickListener fabMenuButtonsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<String, Void, Boolean> addMarker = new ServerAsyncTask.TaskAddMarker();
                String i = "1";
                switch (view.getId()) {
                    case R.id.addMarkId1:
                        i = "1";
                        break;
                    case R.id.addMarkId2:
                        i = "2";
                        break;
                    case R.id.addMarkId3:
                        i = "3";
                        break;
                    case R.id.addMarkId4:
                        i = "4";
                        break;
                    case R.id.addMarkId5:
                        i = "5";
                        break;
                }
                addMarker.execute(token, "" + userId, i);
                fab.startAnimation(rotate_forward);
                isAddFab = false;
                fab.setOnClickListener(fabDeleteMark());
                closeFabMenu();
                try {
                    Log.i(TAG, "AddMarker get" + addMarker.get());
                    if (addMarker.get()) {
                        Intent intent = new Intent(MainActivity.this, MarkerUpdateService.class);
                        intent.putExtra("respondThread", 1);
                        startService(intent);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
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
    }

    private void closeFabMenu() {
        isOpenFabMenu = false;
        fabMenu.setVisibility(View.INVISIBLE);
        myLocButton.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    private void openFabMenu() {
        isOpenFabMenu = true;
        fabMenu.setVisibility(View.VISIBLE);
        myLocButton.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onMarkerClick(int id) {
        Log.i(TAG, "onMarkerClick");
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putInt("userId", userId);
        args.putString("token", token);
        DialogFragment markerClickDialog = new MarkerClickDialog();
        markerClickDialog.setArguments(args);
        markerClickDialog.show(getFragmentManager(), "MarkerClickDialog");
    }

    private void initToolbar(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.main);
    }
    private void setNewResponseIcon() {

    }

    class ChangeListener extends Thread {
        private boolean isStart;

        private MainActivity activity;

        ChangeListener(MainActivity activity) {
            this.activity = activity;
        }

        void stopSelf() {
            isStart = false;
        }
        @Override
        public void run() {
            isStart = true;
            while (isStart) {
                if (MarkerUpdateService.isChange) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appMap.addAllMarker();
                            initFab();
                        }
                    });
                    MarkerUpdateService.isChange = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (MarkerUpdateService.isChangeResponse() && !isNewResponse) {
                    setNewResponseIcon();
                }
            }
            super.run();
        }

    }
}
