package com.tripasfactory.clientcore.maps;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by revs8 on 30/07/2016.
 */
public interface ICCGoogleMapsConfig {
    int getLayout();

    boolean isFusedVisibleByDefault();

    void onCameraChangeImpl();

    GoogleMap.OnCameraChangeListener getOnCameraChangeListener();

    GoogleMap.OnMapLongClickListener getOnMapLongClickListener();

    GoogleMap.OnMapClickListener getOnMapClickListener();
}
