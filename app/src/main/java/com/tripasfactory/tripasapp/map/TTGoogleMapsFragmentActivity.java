package com.tripasfactory.tripasapp.map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tripasfactory.clientcore.maps.CCGoogleMapsFragmentActivity;
import com.tripasfactory.clientcore.maps.ICCGoogleMapsConfig;
import com.tripasfactory.clientcore.maps.ICCMarkerConfig;
import com.tripasfactory.clientcore.maps.animation.CCMarkersAnimator;
import com.tripasfactory.clientcore.maps.animation.ICCMarkersAnimatorCallback;
import com.tripasfactory.tripasapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by revs87 on 30/07/2016.
 */
public class TTGoogleMapsFragmentActivity extends CCGoogleMapsFragmentActivity implements ICCGoogleMapsConfig, ICCMarkerConfig {

    private AtomicBoolean avatarLocationSet = new AtomicBoolean();

    private double avatarLat = 0.0;
    private double avatarLon = 0.0;
    private LatLng avatarPositionMap;
    private Marker avatarPositionMarker;
    private Button avatarSetBtn;
    private boolean avatarLocked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        avatarSetBtn = (Button) findViewById(R.id.avatar_set_btn);
        avatarSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAvatarLocationSet();
            }
        });
        unsetAvatar();
    }

    private void checkAvatarLocationSet() {
        if (isSetAvatar()) {
            unsetAvatar();
        } else {
            setAvatar();
        }
    }

    private void setAvatar() {
        avatarLocationSet.set(true);
        avatarSetBtn.setText("Release avatar");
        if (avatarPositionMarker != null) {
            avatarPositionMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    private void unsetAvatar() {
        avatarLocationSet.set(false);
        avatarLocked = false;
        avatarSetBtn.setText("Set avatar");
        removeDestinationPositionMarker();
    }

    private boolean isSetAvatar() {
        return avatarLocationSet.get();
    }

    @Override
    protected void updateFusedPositionMarker(LatLng latLng) {
        if (isSetAvatar()) {
            fusedPositionMap = latLng;
        } else {
            super.updateFusedPositionMarker(latLng);
        }
    }

    @Override
    public void onCameraChangeImpl() {
        Log.d("onCameraChangeImpl", userPositionMap.latitude + " " + userPositionMap.longitude);
        if (!isSetAvatar()) {
            avatarPositionMap = userPositionMap;
            if (avatarPositionMarker != null) {
                avatarPositionMarker.remove();
            }
            MarkerOptions markerOptions = getAvatarMarkerOptions(avatarPositionMap);
            avatarPositionMarker = mMap.addMarker(markerOptions);
            if (avatarPositionMarker != null) {
                avatarPositionMarker.setVisible(true);
            }
        }
    }

    @Override
    public void onAnimationStart() {
        //goToLocation(latLng);
        List<Marker> markers = new ArrayList<>();
        markers.add(avatarPositionMarker);
        markers.add(destinationPositionMarker);
        animator = new CCMarkersAnimator(mMap, markers, new ICCMarkersAnimatorCallback() {
            @Override
            public void onFinished() {
                updateAvatarToDestination();
            }
        });
        animator.startAnimation();
    }

    private void updateAvatarToDestination() {
        if (avatarPositionMarker != null) {
            avatarPositionMarker.remove();
        }
        avatarPositionMap = destinationPositionMap;
        MarkerOptions markerOptions = getAvatarMarkerOptions(avatarPositionMap);
        avatarPositionMarker = mMap.addMarker(markerOptions);
        if (avatarPositionMarker != null) {
            avatarPositionMarker.setVisible(true);
        }
        removeDestinationPositionMarker();
    }

    @Override
    public void onAnimationStop() {
        if (animator != null) {
            animator.stopAnimation();
        }
    }


    @Override
    public int getLayout() {
        return R.layout.fragment_map;
    }

    @Override
    public boolean isFusedVisibleByDefault() {
        return false;
    }

    @Override
    public MarkerOptions getAvatarMarkerOptions(LatLng latLng) {
        MarkerOptions markerOptions = super.getAvatarMarkerOptions(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        return markerOptions;
    }
}
