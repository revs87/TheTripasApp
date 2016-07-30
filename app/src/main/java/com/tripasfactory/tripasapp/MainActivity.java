package com.tripasfactory.tripasapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.tripasfactory.tripasapp.map.TTGoogleMapsFragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, TTGoogleMapsFragmentActivity.class));
    }

}
