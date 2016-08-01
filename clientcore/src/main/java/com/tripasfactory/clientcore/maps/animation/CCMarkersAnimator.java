package com.tripasfactory.clientcore.maps.animation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by revs87 on 31/07/2016.
 */
public class CCMarkersAnimator {

    private GoogleMap mMap;
    private List<Marker> markers;
    private ICCMarkersAnimatorCallback callback;

    private int currentPt;

    public CCMarkersAnimator(GoogleMap mMap, List<Marker> markers, ICCMarkersAnimatorCallback callback) {
        this.mMap = mMap;
        this.markers = markers;
        this.callback = callback;
    }

    public void startAnimation() {
//        resetMarkers();
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 16),
                1000,
                simpleAnimationCancelableCallback);

        currentPt = 0 - 1;
    }

    public void stopAnimation() {
//        resetMarkers();
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(), 16));
    }

//    private void resetMarkers() {
//        for (int i = 0; i < markers.size(); i++) {
//            unhighLightMarker(markers.get(i));
//        }
//    }

    private GoogleMap.CancelableCallback simpleAnimationCancelableCallback =
            new GoogleMap.CancelableCallback() {

                @Override
                public void onCancel() {
                }

                @Override
                public void onFinish() {

                    if (++currentPt < markers.size()) {

//					double heading = SphericalUtil.computeHeading(mMap.getCameraPosition().target, markers.get(currentPt).getPosition());
//					System.out.println("Heading  = " + (float)heading);
//					float targetBearing = bearingBetweenLatLngs(mMap.getCameraPosition().target, markers.get(currentPt).getPosition());
//					System.out.println("Bearing  = " + targetBearing);
//					
                        LatLng targetLatLng = markers.get(currentPt).getPosition();

                        CameraPosition cameraPosition =
                                new CameraPosition.Builder()
                                        .target(targetLatLng)
                                        .tilt(currentPt < markers.size() - 1 ? 90 : 0)
                                        //.bearing((float)heading)
                                        .zoom(mMap.getCameraPosition().zoom)
                                        .build();


                        mMap.animateCamera(
                                CameraUpdateFactory.newCameraPosition(cameraPosition),
                                5000,
                                simpleAnimationCancelableCallback);

//                        highLightMarker(currentPt);

                        callback.onFinished();
                    }
                }
            };

    /**
     * Highlight the marker by index.
     */
    private void highLightMarker(int index) {
        highLightMarker(markers.get(index));
    }

    /**
     * Highlight the marker by marker.
     */
    private void highLightMarker(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        marker.showInfoWindow();
        //Utils.bounceMarker(googleMap, marker);
//        this.selectedMarker = marker;
    }

    private void unhighLightMarker(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
    }

}
