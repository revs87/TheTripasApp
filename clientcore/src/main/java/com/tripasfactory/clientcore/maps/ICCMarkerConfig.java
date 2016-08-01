package com.tripasfactory.clientcore.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by revs87 on 31/07/2016.
 */
public interface ICCMarkerConfig {

    void onAnimationStart();

    void onAnimationStop();

    MarkerOptions getFusedMarkerOptions(LatLng latLng);

    MarkerOptions getDestinationMarkerOptions(LatLng latLng);

    MarkerOptions getAvatarMarkerOptions(LatLng latLng);
}
