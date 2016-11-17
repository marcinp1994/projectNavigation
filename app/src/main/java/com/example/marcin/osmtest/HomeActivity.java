package com.example.marcin.osmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import static com.example.marcin.osmtest.HTTPConnectionToRemoteServer.getNotConnected;

public class HomeActivity extends AppCompatActivity {
    private int statusFromTest;



    private static String OSRM_OR_MAPQUEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RoutingByOSRM testRouting = new RoutingByOSRM(this);
        ArrayList<GeoPoint> testWaypoints = new ArrayList<>();
        testWaypoints.add(new GeoPoint(20.0,21.0));
        testWaypoints.add(new GeoPoint(22.0,23.0));
        statusFromTest = testRouting.getRoad(testWaypoints).roadStatus;
        String statusConnectionToHTTP = getNotConnected();
        if(statusFromTest == ResponseStatus.INVALID.getValue() || statusConnectionToHTTP.equals("NotConnected"))
        {
            OSRM_OR_MAPQUEST = "MAP_QUEST";
        }
        else
        {
            OSRM_OR_MAPQUEST = "OSRM";
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.historia:
            {
                Intent goToIntent = new Intent(this, HistoryActivity.class);
                startActivity(goToIntent);
                break;
            }
            case R.id.menu:
            {
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showMapOnClick(View view)
    {
        Intent goToIntent = new Intent(this, MainActivity.class);
        startActivity(goToIntent);
    }

    public void navByCarOnClick(View view)
    {
        Intent goToIntent = new Intent(this, NavCarActivity.class);
        startActivity(goToIntent);
    }
    public void navByBikeOnClick(View view)
    {
        Intent goToIntent = new Intent(this, NavBikeActivity.class);
        startActivity(goToIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu3, menu);
        return true;
    }
    public static String getOsrmOrMapquest() {
        return OSRM_OR_MAPQUEST;
    }
}

