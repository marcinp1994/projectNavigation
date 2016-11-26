package com.example.marcin.osmtest;

import android.location.Location;
import android.os.AsyncTask;

/**
 * Created by Marcin on 26.11.2016.
 */

public class RoadInfoAsyncTask extends AsyncTask<Location,Float,Float>
{
    private static Location previousLocation = null;
    private Location currentLocation = null;
    private static long previousTime = 0;
    private float time = 0;
    private float distance = 0;
    @Override
    protected Float doInBackground(Location locations[])
    {
        currentLocation = locations[0];
        if(previousLocation == null) {
            previousLocation = currentLocation;
            return null;
        }
        distance = previousLocation.distanceTo(currentLocation);
        time = (System.currentTimeMillis() - previousTime) / 1000f;
        previousTime = System.currentTimeMillis();
        previousLocation = currentLocation;
        return distance;
    }

    protected void onPostExecute(Float result)
    {
        if(result != null) {
            NavActivity.lengthOfRoad -= (result/1000);
            String lengthText = RoadDescription.getLenAndDurAsString(NavBikeActivity.context, NavActivity.lengthOfRoad, NavActivity.duration);
            String speedText = "";
            if(time > 0) {
                float speed = Math.round(distance / time * 3.6f * 10f) / 10f;
                float locationSpeed = Math.round(currentLocation.getSpeed() * 3.6f * 10f) / 10f;
                speedText = String.valueOf(speed) + " km/h, " + String.valueOf(locationSpeed);
                //Toast.makeText(NavActivity.context, String.valueOf(time), Toast.LENGTH_LONG).show();
            }

            NavActivity.routeInfo.setTextColor(0xFF0000);
            NavActivity.routeInfo.setText(lengthText + "\n                " + speedText);
        }

    }

}
