package com.tripasfactory.clientcore.maps;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.tripasfactory.clientcore.maps.animation.CCMarkersAnimator;
import com.tripasfactory.clientcore.maps.animation.ICCMarkersAnimatorCallback;
import com.tripasfactory.core.permissions.PermissionsChecker;
import com.tripasfactory.core.permissions.PermissionsHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by revs87 on 30/07/2016.
 */
public class CCGoogleMapsFragmentActivity extends CCFusedLocationFragmentActivity implements ICCGoogleMapsConfig, ICCMarkerConfig {

    protected static final int WALKING_ZOOM_VALUE = 16;

    protected Vibrator mVibrator;

    protected GoogleMap mMap;

    protected float lastZoom = WALKING_ZOOM_VALUE;
    protected LatLng userPositionMap;

    protected LatLng fusedPositionMap;
    protected Marker fusedPositionMarker;
    protected LatLng destinationPositionMap;
    protected Marker destinationPositionMarker;

    protected Activity activity;
    protected CCMarkersAnimator animator;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissionsChecker != null) {
            permissionsChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

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
        mMap.setOnCameraChangeListener(getOnCameraChangeListener());
        mMap.setOnMapLongClickListener(getOnMapLongClickListener());
        mMap.setOnMapClickListener(getOnMapClickListener());

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
        Log.d("FusedCurrentPositionMap", "lat:" + realLat + " lon:" + realLon);
        fusedPositionMap = new LatLng(realLat, realLon);

        updateFusedPositionMarker(fusedPositionMap);
    }

    /**
     * Move camera to point
     */
    protected void goToLocation(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, this.lastZoom);
        mMap.animateCamera(cameraUpdate);
    }

    private void goToRealLocation() {
        // Create a LatLng object for the current location
        LatLng latLng = new LatLng(realLat, realLon);

        updateFusedPositionMarker(latLng);
    }

    /**
     * Markers
     */
    protected void updateFusedPositionMarker(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, WALKING_ZOOM_VALUE);
        mMap.animateCamera(cameraUpdate);

        if (fusedPositionMarker != null) {
            fusedPositionMarker.remove();
        }
        MarkerOptions markerOptions = getFusedMarkerOptions(latLng);
        fusedPositionMarker = mMap.addMarker(markerOptions);
        if (fusedPositionMarker != null) {
            fusedPositionMarker.setVisible(isFusedVisibleByDefault());
            fusedPositionMarker.showInfoWindow();
        }
    }

    protected void addDestinationPositionMarker(LatLng latLng) {
        removeDestinationPositionMarker();
        MarkerOptions markerOptions = getDestinationMarkerOptions(latLng);
        destinationPositionMarker = mMap.addMarker(markerOptions);
        if (destinationPositionMarker != null) {
            destinationPositionMarker.showInfoWindow();
        }
    }

    protected void removeDestinationPositionMarker() {
        if (destinationPositionMarker != null) {
            destinationPositionMarker.remove();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_map;
    }

    @Override
    public boolean isFusedVisibleByDefault() {
        return true;
    }

    @Override
    public void onCameraChangeImpl() {
    }

    @Override
    public GoogleMap.OnCameraChangeListener getOnCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                userPositionMap = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                lastZoom = cameraPosition.zoom;
                onCameraChangeImpl();
            }
        };
    }

    @Override
    public GoogleMap.OnMapLongClickListener getOnMapLongClickListener() {
        return new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                destinationPositionMap = latLng;
                addDestinationPositionMarker(latLng);

                // vibrate 25ms
                mVibrator.vibrate(25);

                // Cute animation
                onAnimationStart();
            }
        };
    }

    @Override
    public GoogleMap.OnMapClickListener getOnMapClickListener() {
        return new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                destinationPositionMap = null;
                removeDestinationPositionMarker();
                onAnimationStop();
            }
        };
    }

    @Override
    public void onAnimationStart() {
        //goToLocation(latLng);
        List<Marker> markers = new ArrayList<>();
        markers.add(fusedPositionMarker);
        markers.add(destinationPositionMarker);
        animator = new CCMarkersAnimator(mMap, markers, new ICCMarkersAnimatorCallback() {
            @Override
            public void onFinished() {

            }
        });
        animator.startAnimation();
    }

    @Override
    public void onAnimationStop() {
        if(animator != null){
            animator.stopAnimation();
        }

    }

    @Override
    public MarkerOptions getFusedMarkerOptions(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("You are here!");
        return markerOptions;
    }

    @Override
    public MarkerOptions getDestinationMarkerOptions(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Destination?");
        return markerOptions;
    }

    @Override
    public MarkerOptions getAvatarMarkerOptions(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        return markerOptions;
    }
}
