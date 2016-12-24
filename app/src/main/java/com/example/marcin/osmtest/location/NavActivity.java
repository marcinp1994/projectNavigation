package com.example.marcin.osmtest.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcin.osmtest.BuildConfig;
import com.example.marcin.osmtest.R;
import com.example.marcin.osmtest.activity.HistoryActivity;
import com.example.marcin.osmtest.activity.ItemRoadStepActivity;
import com.example.marcin.osmtest.activity.POIActivity;
import com.example.marcin.osmtest.activity.SearchActivity;
import com.example.marcin.osmtest.routing.RoadDescription;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;
import java.util.List;

import static com.example.marcin.osmtest.location.LocationHolder.location;

public abstract class NavActivity extends AppCompatActivity implements LocationListener {
    public static final String keyForMapQuest = "ETefQk4KAr64RQryy3gD1tbwDZsqA0IX";
    public static final int REQUEST_CHECK_SETTINGS = 0x99;
    public static final int REQUEST_POI = 9;
    public static double lengthOfRoad = 0;
    public static double duration = 0;
    public static TextView routeInfo;
    public static Context context;
    public static List<RoadNode> listOfRoadNodes = new ArrayList<>();
    private static Location previousLocation = null;
    private static long previousTime = 0;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    protected FolderOverlay mRoadNodeMarkers;
    OnlineTileSourceBase MAPBOXSATELLITELABELLED;
    MapView map;
    ArrayList<GeoPoint> waypoints = new ArrayList<>();
    IMapController mapController;
    RoadManager roadManagerForMapQuest;
    Road road = null;
    Drawable icon;
    MarkerInfoWindow infoWindow;
    GeoPoint startPoint;
    Marker positionMarker = null;
    RoadNode nextNode = null;
    RoadNode previousNode = null;
    android.support.v7.widget.Toolbar toolbar;
    String poi;
    double lon;
    double lat;
    Location lastKnownLocation;
    Location myLocation = LocationHolder.location;
    Polyline roadPolyline;
    GeoPoint endPoint;
    SensorManager sensorManager;
    private Location currentLocation = null;
    private float time = 0;
    private float distance = 0;

    public abstract int getRequestCode();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OpenStreetMapTileProviderConstants.setCachePath(this.getFilesDir().getAbsolutePath());
        OpenStreetMapTileProviderConstants.setCacheSizes(1000L, 900L);
        OpenStreetMapTileProviderConstants.setOfflineMapsPath("/storage/extSdCard/osmdroid");
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        startLocationListener();

        //Introduction
        setContentView(R.layout.activity_nav);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        MAPBOXSATELLITELABELLED = new MapBoxTileSource("MapBoxSatelliteLabelled", 1, 19, 256, ".png");
        ((MapBoxTileSource) MAPBOXSATELLITELABELLED).retrieveAccessToken(this);
        ((MapBoxTileSource) MAPBOXSATELLITELABELLED).retrieveMapBoxMapId(this);
        TileSourceFactory.addTileSource(MAPBOXSATELLITELABELLED);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        context = this;


        final Button startNavigation = (Button) findViewById(R.id.startNavigation);
        final Button centered = (Button) findViewById(R.id.centered);

        startNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNavigation.setVisibility(View.INVISIBLE);
                centered.setVisibility(View.VISIBLE);
                mapController.setCenter(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                mapController.setZoom(18);
            }
        });

        centered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapController.setCenter(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                mapController.animateTo(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
                mapController.setZoom(18);

            }
        });

        map = (MapView) findViewById(R.id.map);
        icon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
        infoWindow = new MarkerInfoWindow(R.layout.bubble, map);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();

        mapController.setZoom(13);
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        map.setVisibility(View.INVISIBLE);


        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(map);
        map.getOverlays().add(scaleBarOverlay);

        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        routeInfo = (TextView) findViewById(R.id.routeInfo);

        setStartPoint(location);
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        addPositionMarker(geoPoint);

        Intent intent = getIntent();
        lon = intent.getDoubleExtra("lon", 0.0);
        lat = intent.getDoubleExtra("lat", 0.0);
        if (lon == 0.0 && lat == 0.0) {
            Intent goToIntent = new Intent(this, SearchActivity.class);
            startActivityForResult(goToIntent, getRequestCode());
        } else {
            onAddressRecived(lat, lon);
        }

        SensorListener mySensorEventListener = new SensorListener() {

            @Override
            public void onSensorChanged(int sensor, float[] values) {
                synchronized (this) {
                    float mHeading = values[0];
                    map.setMapOrientation(-mHeading);
                }
            }

            @Override
            public void onAccuracyChanged(int i, int i1) {

            }
        };

        sensorManager.registerListener(mySensorEventListener,
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_UI);


    }

    private void setStartPoint(Location location) {
        if (startPoint != null) {
            map.getOverlays().remove(startPoint);
            waypoints.remove(startPoint);
        }
        startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setIcon(getResources().getDrawable(R.drawable.start));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        startMarker.setTitle("Start point");
        startMarker.setDraggable(true);
        map.getOverlays().add(startMarker);
        waypoints.add(startPoint);
    }

    public void startLocationListener() {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation == null) {
                lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
    }

    public void stopLocationListener() {
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        }
        locationManager.removeUpdates(this);
    }

    public void onAddressRecived(GeoPoint endPoint) {
        onAddressRecived(endPoint.getLatitude(), endPoint.getLongitude());
    }

    public void onAddressRecived(double latitude, double longitude) {

    }

    public void recalculateRoad() {
//        stopLocationListener();
        map.getOverlays().clear();
        waypoints.clear();
        setStartPoint(myLocation);
        addPositionMarker(new GeoPoint(myLocation.getLatitude(), myLocation.getLongitude()));
        onAddressRecived(endPoint);
        Toast.makeText(this, "Recalculated!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu4, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.night: {
                map.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);
                map.invalidate();
                break;
            }
            case R.id.satelita: {
                if (!(map.getTileProvider() instanceof MapTileProviderBasic)) {
                    MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
                    map.setTileProvider(bitmapProvider);
                }
                map.setTileSource(MAPBOXSATELLITELABELLED);
                map.getOverlayManager().getTilesOverlay().setColorFilter(null);
                map.invalidate();
                break;
            }
            case R.id.historia: {
                Intent goToIntent = new Intent(this, HistoryActivity.class);
                startActivity(goToIntent);
                break;
            }
            case R.id.listOfRoadStep: {
                Intent goToIntent = new Intent(this, ItemRoadStepActivity.class);
                startActivity(goToIntent);
                break;
            }
            case R.id.poi: {
                Intent goToIntent = new Intent(this, POIActivity.class);
                startActivityForResult(goToIntent, REQUEST_POI);
                break;
            }
            case R.id.poi_menu: {
                Intent goToIntent = new Intent(this, POIActivity.class);
                startActivityForResult(goToIntent, REQUEST_POI);
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    protected void fillNodeRoadInfo(int roadStep, RoadNode node) {
        if (roadStep == 0) {
            nextNode = node;
        }
        if (roadStep == 1) {
            previousNode = node;
        }
        listOfRoadNodes.add(node);
        String instructions = (node.mInstructions == null ? "" : node.mInstructions);
        Marker nodeMarker = new Marker(map);
        nodeMarker.setTitle(getString(R.string.step) + " " + (roadStep + 1));
        nodeMarker.setSnippet(instructions);
        nodeMarker.setSubDescription(RoadDescription.getLenAndDurAsString(context, node.mLength, node.mDuration, true));
        nodeMarker.setPosition(node.mLocation);
        nodeMarker.setIcon(icon);
        nodeMarker.setInfoWindow(infoWindow);

        Drawable directionIcon = RoadDescription.chooseIconForManeuver(node.mManeuverType, context);
        nodeMarker.setImage(directionIcon);
        mRoadNodeMarkers.add(nodeMarker);
    }

    void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        String message = "Application permissions:";
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            message += "\nLocation to show user location.";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.INTERNET);
            message += "\nStorage access to store map tiles.";
        }
        if (!permissions.isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            String[] params = permissions.toArray(new String[permissions.size()]);
            ActivityCompat.requestPermissions(this, params, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        } // else: We already have permissions, so handle as normal
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getRequestCode()) {
            if (resultCode == RESULT_OK) {
                lon = data.getDoubleExtra("lon", 0.0);
                lat = data.getDoubleExtra("lat", 0.0);
                onAddressRecived(lat, lon);
            }
        } else if (requestCode == REQUEST_POI) {
            if (resultCode == RESULT_OK) {
                poi = data.getStringExtra("poi");
                showPOIOnMap();
            }
        }
    }


    private void addPositionMarker(GeoPoint center) {
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
        //   mapController.setZoom(16);

//        if(nextNode != null && isNodesClose(center, nextNode.mLocation)) {
//            int nextIndex = listOfRoadNodes.indexOf(nextNode);
//            map.getOverlays().remove(mRoadNodeMarkers.getItems().get(nextIndex-1));
//            previousNode = nextNode;
//            if(nextIndex != listOfRoadNodes.size()) {
//                nextNode = listOfRoadNodes.get(nextIndex);
//            }
//        }

        map.invalidate();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            myLocation = location;
            GeoPoint locationPoint = new GeoPoint(location.getLatitude(), location.getLongitude());


            if (roadPolyline != null && roadPolyline.getPoints() != null && roadPolyline.getPoints().size() > 0 && !roadPolyline.isCloseTo(locationPoint, 200, map)) {
                recalculateRoad();
            }

//            GeoPoint closestPoint = roadPolyline.getPoints().get(0);
//            float closestPointDistance = locationPoint.distanceTo(closestPoint);
//            for(GeoPoint roadPoint : roadPolyline.getPoints()) {
//                float distance = locationPoint.distanceTo(roadPoint);
//                if(closestPointDistance > distance) {
//                    closestPointDistance = distance;
//                    closestPoint = roadPoint;
//                }
//            }
            //map.getOverlays().
            calculateRoadInfo(location);
            //new RoadInfoAsyncTask().execute(location);
            addPositionMarker(locationPoint);
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

    private boolean isNodesClose(GeoPoint g1, GeoPoint g2) {
        double dMin = 0.001;
        double dLon = Math.abs(g1.getLongitude() - g2.getLongitude());
        double dLat = Math.abs(g1.getLatitude() - g2.getLatitude());

        if (dLon < dMin && dLat < dMin) {
            return true;
        }
        return false;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // sprawdzenie orientacji telefonu
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
            mapController.setCenter(startPoint);

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            toolbar.setVisibility(View.VISIBLE);
            mapController.setCenter(startPoint);
        }
    }

    private void showPOIOnMap() {
        NominatimPOIProvider poiProvider = new NominatimPOIProvider("navigationGPSv1");
        ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, poi, 50, 0.2);
        FolderOverlay poiMarkers = new FolderOverlay(context);
        map.getOverlays().add(poiMarkers);
        Drawable poiIcon = getResources().getDrawable(R.drawable.poismarkers);
        for (POI poi : pois) {
            Marker poiMarker = new Marker(map);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            poiMarker.setIcon(poiIcon);
            poiMarkers.add(poiMarker);
        }
        map.invalidate();
    }

    private void calculateRoadInfo(Location location) {
        currentLocation = location;
        if (previousLocation == null) {
            previousLocation = currentLocation;
            return;
        }
        distance = previousLocation.distanceTo(currentLocation);
        time = (System.currentTimeMillis() - previousTime) / 1000f;

        previousTime = System.currentTimeMillis();
        previousLocation = currentLocation;

        if (distance != 0) {
            lengthOfRoad -= (distance / 1000);
            duration -= 2.0;
            String lengthAndDurationText = RoadDescription.getLenAndDurAsString(context, lengthOfRoad, duration, false);
            String speedText = "";
            if (time > 0) {
                // float speed = Math.round(distance / time * 3.6f * 10f) / 10f;
                float locationSpeed = Math.round(currentLocation.getSpeed() * 3.6f * 10f) / 10f;
                speedText = String.valueOf(locationSpeed) + " km/h";
                //Toast.makeText(NavActivity.context, String.valueOf(time), Toast.LENGTH_LONG).show();
            }

            // NavActivity.routeInfo.setTextColor(0xFF0000);
            routeInfo.setText(lengthAndDurationText + "\n                             " + speedText);
        }
    }


}
