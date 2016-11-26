package com.example.marcin.osmtest;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by Marcin on 23.11.2016.
 */

class LocationHolder implements LocationListener {

    public static Location location = new Location("");
    @Override
    public void onLocationChanged(Location loc)
    {
        if(loc != null)
        {
            location = loc;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        System.out.println("GPS" + "Provider enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        System.out.println("GPS" + "Provider disabled");
    }
}
