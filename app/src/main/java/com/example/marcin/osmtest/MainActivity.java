package com.example.marcin.osmtest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;

import java.util.ArrayList;

import static com.example.marcin.osmtest.LocationHolder.location;
import static com.example.marcin.osmtest.NavActivity.REQUEST_POI;

public class MainActivity extends AppCompatActivity {
    MapView map;
    Context context;
    IMapController mapController;
    Marker positionMarker = null;
    String poi;
    GeoPoint myPosition;
    Location lastKnownLocation = null;
    Location location1;
    LocationListener locationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OpenStreetMapTileProviderConstants.setCachePath(this.getFilesDir().getAbsolutePath());
        OpenStreetMapTileProviderConstants.setCacheSizes(1000L, 900L);
        OpenStreetMapTileProviderConstants.setOfflineMapsPath("/storage/extSdCard/osmdroid");
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button navButton = (Button) findViewById(R.id.nav);
        final Button carButton = (Button) findViewById(R.id.car);
        final Button bikeButton = (Button) findViewById(R.id.bike);

        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);


        GeoPoint startPoint = new GeoPoint(50.06465009999999, 19.94497990000002);
        mapController = map.getController();
        mapController.setZoom(14);
        mapController.setCenter(startPoint);
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        map.invalidate();

        navButton.setVisibility(View.VISIBLE);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carButton.getVisibility() == View.INVISIBLE && bikeButton.getVisibility() == View.INVISIBLE) {
                    carButton.setVisibility(View.VISIBLE);
                    bikeButton.setVisibility(View.VISIBLE);
                } else {
                    carButton.setVisibility(View.INVISIBLE);
                    bikeButton.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_bike: {
                Intent goToIntent = new Intent(this, NavBikeActivity.class);
                startActivity(goToIntent);
                break;
            }
            case R.id.nav_car: {
                Intent goToIntent = new Intent(this, NavCarActivity.class);
                startActivity(goToIntent);
                break;
            }
            case R.id.history: {
                Intent goToIntent = new Intent(this, HistoryActivity.class);
                startActivity(goToIntent);
                break;
            }
            case R.id.night_mode:
            {
                map.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);
                map.invalidate();
                break;
            }
            case R.id.pois:
            {
                Intent goToIntent = new Intent(this, POIActivity.class);
                startActivityForResult(goToIntent, REQUEST_POI);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void showPositionOnClick(View view)
    {

        if(location != null)
        {
            myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapController.setCenter(myPosition);
            addMarker(myPosition);
            map.invalidate();

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            String locationProvider = LocationManager.GPS_PROVIDER;
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location)
                {
                    myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                    addMarker(myPosition);
                    map.invalidate();
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(locationProvider, 1000, 5, locationListener);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

        }
        else
        {
            myPosition = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            addMarker(myPosition);
            map.invalidate();

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            String locationProvider = LocationManager.GPS_PROVIDER;
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location)
                {
                    myPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                    addMarker(myPosition);
                    map.invalidate();
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(locationProvider,0,0, locationListener);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        }

    }

    public void bikeOnClick(View view) {
        Intent goToIntent = new Intent(this, NavBikeActivity.class);
        startActivity(goToIntent);
    }

    public void carOnClick(View view) {
        Intent goToIntent = new Intent(this, NavCarActivity.class);
        startActivity(goToIntent);
    }

    private void addMarker(GeoPoint center) {
        if (positionMarker != null) {
            map.getOverlays().remove(positionMarker);
        }
        positionMarker = new Marker(map);
        positionMarker.setPosition(center);
        positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        positionMarker.setIcon(getResources().getDrawable(R.drawable.runningpoint));
        map.getOverlays().add(positionMarker);
        mapController.setCenter(center);
        mapController.animateTo(center);
        map.invalidate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_POI)
        {
            if (resultCode == RESULT_OK)
            {
                poi = data.getStringExtra("poi");
                showPOIOnMap();
            }
        }
    }

    private void showPOIOnMap() {
        NominatimPOIProvider poiProvider = new NominatimPOIProvider("navigationGPSv1");
        ArrayList<POI> pois = poiProvider.getPOICloseTo(myPosition, poi, 50, 0.2);
        FolderOverlay poiMarkers = new FolderOverlay(context);
        map.getOverlays().add(poiMarkers);
        Drawable poiIcon = getResources().getDrawable(R.drawable.poismarkers);
        for (POI poi:pois)
        {
            Marker poiMarker = new Marker(map);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            poiMarker.setIcon(poiIcon);
            poiMarkers.add(poiMarker);
        }
        map.invalidate();
    }

}