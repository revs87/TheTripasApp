package com.tripasfactory.tripasapp.map;

import com.tripasfactory.clientcore.maps.CCGoogleMapsFragmentActivity;
import com.tripasfactory.clientcore.maps.ICCGoogleMapsConfig;
import com.tripasfactory.tripasapp.R;

/**
 * Created by revs87 on 30/07/2016.
 */
public class TTGoogleMapsFragmentActivity extends CCGoogleMapsFragmentActivity implements ICCGoogleMapsConfig {

    @Override
    public int getLayout() {
        return R.layout.fragment_map;
    }
}
