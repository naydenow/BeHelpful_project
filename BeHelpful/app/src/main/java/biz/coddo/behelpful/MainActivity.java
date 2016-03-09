package biz.coddo.behelpful;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.support.v4.app.FragmentTransaction;

import java.util.concurrent.ExecutionException;

import biz.coddo.behelpful.AppMap.AppMap;
import biz.coddo.behelpful.AppPreference.DefaultPreference;
import biz.coddo.behelpful.ServerApi.DTOUpdateService;
import biz.coddo.behelpful.ServerApi.ServerAsyncTask;
import biz.coddo.behelpful.Dialogs.MarkerClickDialog;

public class MainActivity extends AppCompatActivity implements AppMap.onMarkerClickListener {

    private static final String TAG = "MainActivity";
    private static SharedPreferences sharedPreferences;
    public static int userId;
    private static String token;
    private AppMap appMap;
    private RelativeLayout fabMenu;
    private Animation rotate_forward, rotate_backward;
    private boolean isOpenFabMenu = false;
    private boolean isAddFab = true;
    private FloatingActionButton fab;
    private FloatingActionButton myLocButton;
    private ChangeListener changeListener;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        token = sharedPreferences.getString(DefaultPreference.USER_TOKEN_STRING.getName(),
                DefaultPreference.USER_TOKEN_STRING.getStringValue());
        Log.i(TAG, token);
        userId = sharedPreferences.getInt(DefaultPreference.USER_ID_INT.getName(),
                DefaultPreference.USER_ID_INT.getIntValue());
        Log.i(TAG, "" + userId);


        initToolbar();

        initFabMenu();

        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_main_rotate_backward);
        initFab();

        initMapFragment();

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
        int i = sharedPreferences.getInt(DefaultPreference.MARKER_UPGRADE_PERIOD_ONPAUSE_MIN_INT.getName(),
                DefaultPreference.MARKER_UPGRADE_PERIOD_ONPAUSE_MIN_INT.getIntValue());
        DTOUpdateService.setUpgradePeriod(i);
        changeListener.stopSelf();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int i = sharedPreferences.getInt(DefaultPreference.MARKER_UPGRADE_PERIOD_ONRESUME_MIN_INT.getName(),
                DefaultPreference.MARKER_UPGRADE_PERIOD_ONRESUME_MIN_INT.getIntValue());
        DTOUpdateService.setUpgradePeriod(i);
        changeListener = new ChangeListener(this);
        changeListener.start();

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
        int i = sharedPreferences.getInt(DefaultPreference.MARKER_UPGRADE_PERIOD_BACKGROUND_MIN_INT.getName(),
                DefaultPreference.MARKER_UPGRADE_PERIOD_BACKGROUND_MIN_INT.getIntValue());
        DTOUpdateService.setUpgradePeriod(i);
    }

    private void initMapFragment() {
        int i = sharedPreferences.getInt(DefaultPreference.APP_MAP_INT.getName(),
                DefaultPreference.APP_MAP_INT.getIntValue());
        appMap = AppMap.getMap(i);
        int container = AppMap.getAppMapContainer(i);
        Log.i(TAG, "Fragment Transaction");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(container, appMap);
        ft.commit();

    }

    private void initFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        setFabListener();

    }

    private void setFabListener(){
        if (DTOUpdateService.isMyMarker()) {
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
                        Intent intent = new Intent(MainActivity.this, DTOUpdateService.class);
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

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_responses:
                        startActivity(new Intent(MainActivity.this, ResponseActivity.class));
                        break;
                    case R.id.menu_settings:
                        break;
                    case R.id.menu_exit:
                        finish();
                        break;
                }
                return true;
            }
        });
        if(DTOUpdateService.isChangeResponse()) setNewResponseIcon();
    }

    private void setNewResponseIcon() {
        toolbar.getMenu().findItem(R.id.menu_responses).setIcon(R.mipmap.ic_email_outline);
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
            Log.i(TAG, "ChangeListener start");
            isStart = true;
            while (isStart) {
                if (DTOUpdateService.isChangeMarkerMap) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            appMap.addAllMarker();
                            setFabListener();
                            Log.i(TAG, "updateMap");
                        }
                    });
                    DTOUpdateService.isChangeMarkerMap = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (DTOUpdateService.isChangeResponse()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setNewResponseIcon();
                        }
                    });
                }
            }
            Log.i(TAG, "ChangeListener start");
            super.run();
        }

    }
}
