package biz.coddo.behelpful;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import biz.coddo.behelpful.AppPreference.DefaultPreference;
import biz.coddo.behelpful.ServerApi.DTOUpdateService;
import biz.coddo.behelpful.Dialogs.GMapNotInstalledDialog;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "startActivity";
    static Fragment regFragment, regConfirmFragment;
    static String userPhone, token;
    static int userId;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        // Check GooglePlayServices available
        int gooPlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
        if (gooPlayServicesAvailable == ConnectionResult.SUCCESS) {
            Log.i(TAG, "gooPlayServicesAvailable SUCCESS");
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(gooPlayServicesAvailable)) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, gooPlayServicesAvailable, -1).show();
            Log.i(TAG, "gooPlayServicesAvailable False, Show ErrorDialog");
        } else {
            Log.i(TAG, "gooPlayServicesAvailable False");
            //TODO change it when App can works without PlayServices
            editor.putBoolean(DefaultPreference.PLAY_SERVICES_AVAILABLE_BOOLEAN.getName(), false);
            editor.commit();
            finish();
        }

        //Check GoogleMap installed
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.google.android.apps.maps", 0);
            Log.i(TAG, "GMapsInstalled True");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "GMapsInstalled False");
            GMapNotInstalledDialog dialogGoogleMapsNotInstalled = new GMapNotInstalledDialog();
            dialogGoogleMapsNotInstalled.show(getFragmentManager(), "dialogGoogleMapsNotInstalled");
            editor.putBoolean(DefaultPreference.GMAP_INSTALLED_BOOLEAN.getName(), false);
            finish();
        }

        //Check fistTimeLoadingApp
        boolean firstTimeLoadingApp = sharedPreferences
                .getBoolean(DefaultPreference.FIRST_TIME_LOADING_BOOLEAN.getName(),
                        DefaultPreference.FIRST_TIME_LOADING_BOOLEAN.isBooleanValue());
        if (firstTimeLoadingApp) {
            regFragment = new RegFragment();
            regConfirmFragment = new RegConfirmFragment();
            Log.i(TAG, "firstTimeLoadingApp");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.activity_start_reg, regFragment);
            fragmentTransaction.commit();

        } else {
            startDTOServices();
            startMainActivity();
        }
    }

    public void registrationComplete() {
        editor.putBoolean(DefaultPreference.FIRST_TIME_LOADING_BOOLEAN.getName(), false);
        editor.putInt(DefaultPreference.USER_ID_INT.getName(), userId);
        editor.putString(DefaultPreference.USER_TOKEN_STRING.getName(), token);
        editor.commit();
        startDTOServices();
        startMainActivity();
    }

    private void startMainActivity() {
        startService(new Intent(this, DTOUpdateService.class));
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void startDTOServices() {
        startService(new Intent(this, DTOUpdateService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
