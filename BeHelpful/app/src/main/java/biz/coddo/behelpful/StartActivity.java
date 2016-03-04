package biz.coddo.behelpful;

import android.content.Context;
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

import biz.coddo.behelpful.ServerApi.MarkerUpdateService;
import biz.coddo.behelpful.dialogs.GMapNotInstalledDialog;

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

        // Check GooglePlayServices available
        int gooPlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getBaseContext());
        if (gooPlayServicesAvailable == ConnectionResult.SUCCESS) {
            Log.i(TAG, "gooPlayServicesAvailable SUCCESS");
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(gooPlayServicesAvailable)) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, gooPlayServicesAvailable, -1).show();
            Log.i(TAG, "gooPlayServicesAvailable False, Show ErrorDialog");
        } else {
            Log.i(TAG, "gooPlayServicesAvailable False");
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
            finish();
        }

        //Check fistTimeLoadingApp
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        boolean firstTimeLoadingApp = sharedPreferences.getBoolean("firstTimeLoadingApp", true);
        if (firstTimeLoadingApp) {
            regFragment = new RegFragment();
            regConfirmFragment = new RegConfirmFragment();
            Log.i(TAG, "firstTimeLoadingApp");
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.activity_start_reg, regFragment);
            fragmentTransaction.commit();

        } else {
            token = sharedPreferences.getString("userToken", null);
            userId = sharedPreferences.getInt("userId", 0);
            startService(new Intent(this, MarkerUpdateService.class));
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("userToken", token);
            startActivity(intent);
        }
    }

    public static void registrationComplit(Context mContext) {
        editor.putBoolean("firstTimeLoadingApp", false);
        editor.putInt("userId", userId);
        editor.putString("userToken", token);
        editor.commit();
        mContext.startService(new Intent(mContext, MarkerUpdateService.class));
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("userToken", token);
        mContext.startActivity(intent);
    }
}
