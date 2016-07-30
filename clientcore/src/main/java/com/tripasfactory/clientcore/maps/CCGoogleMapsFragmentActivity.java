package com.tripasfactory.clientcore.maps;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tripasfactory.clientcore.R;
import com.tripasfactory.core.permissions.PermissionsChecker;
import com.tripasfactory.core.permissions.PermissionsHandler;


/**
 * Created by revs87 on 30/07/2016.
 */
public class CCGoogleMapsFragmentActivity extends CCFusedLocationFragmentActivity implements ICCGoogleMapsConfig {

    private final int WALKING_ZOOM_VALUE = 16;

    private Vibrator mVibrator;

    private GoogleMap mMap;

    private LatLng lastLocation;
    private float lastZoom = WALKING_ZOOM_VALUE;

    private LatLng currentPositionMap;
    private Marker currentPositionMarker;
    private LatLng destinationPositionMap;
    private Marker destinationPositionMarker;

    private LayoutInflater inflater;
    private View rootView;
    private Context activity;
    private PermissionsChecker permissionsChecker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        permissionsChecker = new PermissionsChecker(this, new PermissionsHandler() {
            @Override
            public void setVibrator() {
                setVibratorImpl();
            }

            @Override
            public void setMyLocationButton() {
                setMyLocationButtonImpl();
            }
        });

        setContentView(getLayout());

        permissionsChecker.checkVibratorPermissions();

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

        permissionsChecker.checkLocationPermissions();
    }

    /**
     * Vibrator set
     */
    private void setVibratorImpl() {
        mVibrator = (Vibrator) activity.getSystemService(VIBRATOR_SERVICE);
    }

    /**
     * MyLocation set
     */
    private void setMyLocationButtonImpl() {
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

    @Override
    protected void updateFusedCurrentPositionMap() {
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

    @Override
    public int getLayout() {
        return R.layout.fragment_map;
    }
}
