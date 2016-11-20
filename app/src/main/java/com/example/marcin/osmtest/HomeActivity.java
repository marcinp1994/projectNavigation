package com.example.marcin.osmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static com.example.marcin.osmtest.HTTPConnectionToRemoteServer.getStatusResponse;

public class HomeActivity extends AppCompatActivity {
    private int statusFromTest;

    private static String OSRM_OR_MAPQUEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String testConnection = "https://router.project-osrm.org/route/v1/driving/13.388860,52.517037;13.385983,52.496891?steps=true&alternatives=true";
        new HttpConnectionAsyncTask(testConnection).execute();
        statusFromTest = getStatusResponse();
        if(statusFromTest == ResponseStatus.OK.getValue())
        {
            OSRM_OR_MAPQUEST = "OSRM";
        }
        else
        {
            OSRM_OR_MAPQUEST = "MAP_QUEST";
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

