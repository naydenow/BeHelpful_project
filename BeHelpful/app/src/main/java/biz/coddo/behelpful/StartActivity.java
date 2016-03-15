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
import biz.coddo.behelpful.dialogs.GMapNotInstalledDialog;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "startActivity";
    static Fragment regFragment, regConfirmFragment;
    static String userPhone, token;
    static int userId;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
            //TODO change when App can works without googleMap;
            finish();
        }

        //Check fistTimeLoadingApp
        boolean firstTimeLoadingApp = sharedPreferences
                .getBoolean(DefaultPreference.FIRST_TIME_LOADING_BOOLEAN.getName(),
                        DefaultPreference.FIRST_TIME_LOADING_BOOLEAN.isBooleanValue());
        if (firstTimeLoadingApp) {
            Log.i(TAG, "firstTimeLoadingApp");
            userPhone = sharedPreferences.getString(DefaultPreference.USER_PHONE_STRING.getName(),
                    DefaultPreference.USER_PHONE_STRING.getStringValue());
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            regFragment = new RegFragment();
            regConfirmFragment = new RegConfirmFragment();
            if (userPhone == null) {
                fragmentTransaction.add(R.id.activity_start_reg, regFragment);
            } else {
                fragmentTransaction.add(R.id.activity_start_reg, regConfirmFragment);
            }
            fragmentTransaction.commit();

        } else {
            startDTOServices();
            startMainActivity();
        }
    }

    public boolean isNewPhone() {

        boolean mBoolean = false;
        String mUserPhone = sharedPreferences.getString(DefaultPreference.USER_PHONE_STRING.getName(),
                DefaultPreference.USER_PHONE_STRING.getStringValue());
        if (mUserPhone == null) {
            mBoolean = true;
            editor.putString(DefaultPreference.USER_PHONE_STRING.getName(), userPhone);
            editor.putLong(DefaultPreference.REGISTRATION_TIME_LONG.getName(), System.currentTimeMillis());
        } else if (!userPhone.equals(mUserPhone)) {
            mBoolean = true;
            editor.putBoolean(DefaultPreference.REGISTRATION_STOP_TODAY_BOOLEAN.getName(), true);
        }
        editor.commit();
        return mBoolean;
    }

    public void regFailed(){
        String mUserPhone = sharedPreferences.getString(DefaultPreference.USER_PHONE_STRING.getName(),
                DefaultPreference.USER_PHONE_STRING.getStringValue());
        if (mUserPhone != null) {
            if (sharedPreferences.getBoolean(DefaultPreference.REGISTRATION_STOP_TODAY_BOOLEAN.getName(),
                    DefaultPreference.REGISTRATION_STOP_TODAY_BOOLEAN.isBooleanValue())) {
                editor.remove(DefaultPreference.REGISTRATION_STOP_TODAY_BOOLEAN.getName());
            } else {
                editor.remove(DefaultPreference.USER_PHONE_STRING.getName());
                editor.remove(DefaultPreference.REGISTRATION_TIME_LONG.getName());
            }
        }
        editor.commit();
    }

    public boolean isRegStopToday() {
        boolean mBoolean = false;
        if (sharedPreferences.getBoolean(DefaultPreference.REGISTRATION_STOP_TODAY_BOOLEAN.getName(),
                DefaultPreference.REGISTRATION_STOP_TODAY_BOOLEAN.isBooleanValue())) {
            Long time = sharedPreferences.getLong(DefaultPreference.REGISTRATION_TIME_LONG.getName(), 0);
            if (System.currentTimeMillis() - time > 24 * 60 * 60 * 1000) {
                editor.remove(DefaultPreference.USER_PHONE_STRING.getName());
                editor.remove(DefaultPreference.REGISTRATION_TIME_LONG.getName());
                editor.remove(DefaultPreference.REGISTRATION_STOP_TODAY_BOOLEAN.getName());
            } else mBoolean = true;
        }
        editor.commit();
        return mBoolean;
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
