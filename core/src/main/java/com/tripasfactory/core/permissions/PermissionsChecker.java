package com.tripasfactory.core.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

/**
 * Created by revs8 on 30/07/2016.
 */
public class PermissionsChecker {
    private final int PERMISSION_MYLOCATION_CODE = 100;
    private final int PERMISSION_VIBRATOR_CODE = 101;
    private final Activity context;
    private final PermissionsHandler handler;

    public PermissionsChecker(Activity context, PermissionsHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    /**
     * Permissions
     */
    public void checkVibratorPermissions() {
        if (getVibratorSharedPreference() ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            handler.setVibrator();
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.VIBRATE}, PERMISSION_VIBRATOR_CODE);
        }
    }

    public void checkLocationPermissions() {
        if (getMyLocationSharedPreference() ||
                (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            handler.setMyLocationButton();
        } else {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_MYLOCATION_CODE);
        }
    }

    private boolean getVibratorSharedPreference() {
        SharedPreferences prefs = context.getPreferences(context.MODE_PRIVATE);
        return prefs.getBoolean(String.valueOf(PERMISSION_VIBRATOR_CODE), false);
    }

    private void setVibratorSharedPreference(boolean allowed) {
        SharedPreferences.Editor editor = context.getPreferences(context.MODE_PRIVATE).edit();
        editor.putBoolean(String.valueOf(PERMISSION_VIBRATOR_CODE), allowed);
        editor.commit();
    }

    private boolean getMyLocationSharedPreference() {
        SharedPreferences prefs = context.getPreferences(context.MODE_PRIVATE);
        return prefs.getBoolean(String.valueOf(PERMISSION_MYLOCATION_CODE), false);
    }

    private void setMyLocationSharedPreference(boolean allowed) {
        SharedPreferences.Editor editor = context.getPreferences(context.MODE_PRIVATE).edit();
        editor.putBoolean(String.valueOf(PERMISSION_MYLOCATION_CODE), allowed);
        editor.commit();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_MYLOCATION_CODE) {
            if (permissions.length == 2 &&
                    Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[0]) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[1]) &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationSharedPreference(true);
                handler.setMyLocationButton();
            } else {
                // Permission was denied. Display an error message.
                setMyLocationSharedPreference(false);
            }
        } else if (requestCode == PERMISSION_VIBRATOR_CODE) {
            if (permissions.length == 1 &&
                    Manifest.permission.VIBRATE.equals(permissions[0]) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setVibratorSharedPreference(true);
                handler.setVibrator();
            } else {
                // Permission was denied. Display an error message.
                setVibratorSharedPreference(false);
            }
        }
    }

}
