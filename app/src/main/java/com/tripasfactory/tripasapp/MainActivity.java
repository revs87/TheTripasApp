package com.tripasfactory.tripasapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final int PERMISSION_MYLOCATION_CODE = 100;
    private final int PERMISSION_VIBRATOR_CODE = 101;
    private final int WALKING_ZOOM_VALUE = 16;
//    private final int REFRESHED_TH_FusedPositionMapTimesTH = 5;

    private Vibrator mVibrator;

    private GoogleMap mMap;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double lat = 0.0;
    private double lon = 0.0;
    private LatLng lastLocation;
    private float lastZoom = WALKING_ZOOM_VALUE;

    private LatLng currentPositionMap;
    private Marker currentPositionMarker;
    private LatLng destinationPositionMap;
    private Marker destinationPositionMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkVibratorPermissions();

        buildGoogleApiClient();

        initializeAsyncMap();
    }

    private void initializeAsyncMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if (mMap != null) {
                    setupMap();
                }
            }
        });
    }

    private void setupMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(true);
        mMap.setBuildingsEnabled(false);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                currentPositionMap = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                lastZoom = cameraPosition.zoom;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                destinationPositionMap = latLng;
                addDestinationPositionMarker(latLng);
                goToLocation(latLng);

                // vibrate 25ms
                mVibrator.vibrate(25);
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                destinationPositionMap = null;
                removeDestinationPositionMarker();
            }
        });

        checkLocationPermissions();
    }

    /**
     * Vibrator set
     */
    private void setVibrator() {
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * MyLocation set
     */
    private void setMyLocationButton() {
//        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//            @Override
//            public boolean onMyLocationButtonClick() {
//                goToRealLocation();
//                return true;
//            }
//        });
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    /**
     * Permissions
     */
    private void checkVibratorPermissions() {
        if (getVibratorSharedPreference() ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            setVibrator();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, PERMISSION_VIBRATOR_CODE);
        }
    }

    private void checkLocationPermissions() {
        if (getMyLocationSharedPreference() ||
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            setMyLocationButton();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_MYLOCATION_CODE);
        }
    }

    private boolean getVibratorSharedPreference() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getBoolean(String.valueOf(PERMISSION_VIBRATOR_CODE), false);
    }

    private void setVibratorSharedPreference(boolean allowed) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putBoolean(String.valueOf(PERMISSION_VIBRATOR_CODE), allowed);
        editor.commit();
    }

    private boolean getMyLocationSharedPreference() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        return prefs.getBoolean(String.valueOf(PERMISSION_MYLOCATION_CODE), false);
    }

    private void setMyLocationSharedPreference(boolean allowed) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putBoolean(String.valueOf(PERMISSION_MYLOCATION_CODE), allowed);
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_MYLOCATION_CODE) {
            if (permissions.length == 2 &&
                    Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[0]) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[1]) &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationSharedPreference(true);
                setMyLocationButton();
            } else {
                // Permission was denied. Display an error message.
                setMyLocationSharedPreference(false);
            }
        } else if (requestCode == PERMISSION_VIBRATOR_CODE) {
            if (permissions.length == 1 &&
                    Manifest.permission.VIBRATE.equals(permissions[0]) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setVibratorSharedPreference(true);
                setVibrator();
            } else {
                // Permission was denied. Display an error message.
                setVibratorSharedPreference(false);
            }
        }
    }

    /**
     * Fused Locations
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
        }

        updateFusedCurrentPositionMap();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        updateFusedCurrentPositionMap();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    void updateFusedCurrentPositionMap() {
        Log.d("FusedCurrentPositionMap", "lat:" + lat + " lon:" + lon);
        currentPositionMap = new LatLng(lat, lon);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPositionMap, WALKING_ZOOM_VALUE);
        mMap.animateCamera(cameraUpdate);

        updateCurrentPositionMarker(currentPositionMap);
    }

    /**
     * Move camera to point
     */
    private void goToLocation(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, this.lastZoom);
        mMap.animateCamera(cameraUpdate);
    }

    private void goToRealLocation() {
        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(lat, lon);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, WALKING_ZOOM_VALUE);
        mMap.animateCamera(cameraUpdate);

        updateCurrentPositionMarker(latLng);
    }

    /**
     * Markers
     */
    private void updateCurrentPositionMarker(LatLng latLng) {
        if (currentPositionMarker != null) {
            currentPositionMarker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here!");
        currentPositionMarker = mMap.addMarker(markerOptions);
        if (currentPositionMarker != null) {
            currentPositionMarker.showInfoWindow();
        }
    }

    private void addDestinationPositionMarker(LatLng latLng) {
        removeDestinationPositionMarker();
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Destination?");
        destinationPositionMarker = mMap.addMarker(markerOptions);
        if (destinationPositionMarker != null) {
            destinationPositionMarker.showInfoWindow();
        }
    }

    private void removeDestinationPositionMarker() {
        if (destinationPositionMarker != null) {
            destinationPositionMarker.remove();
        }
    }


}
