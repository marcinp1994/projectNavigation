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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
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
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import java.util.ArrayList;
import java.util.List;

import static com.example.marcin.osmtest.LocationHolder.location;

public abstract class NavActivity extends AppCompatActivity  implements LocationListener {
    public static final String keyForMapQuest = "ETefQk4KAr64RQryy3gD1tbwDZsqA0IX";

    OnlineTileSourceBase MAPBOXSATELLITELABELLED;
    MapView map;
    double lengthOfRoad = 0;
    double duration = 0;
    protected FolderOverlay mRoadNodeMarkers;
    TextView routeInfo;
    ArrayList<GeoPoint> waypoints = new ArrayList<>();
    Context context;
    IMapController mapController;
    RoadManager roadManagerForMapQuest;
    Road road = null;
    Drawable icon;
    MarkerInfoWindow infoWindow;
    protected static final int REQUEST_CHECK_SETTINGS = 0x99;

    Marker positionMarker = null;
    RoadNode nextNode = null;
    RoadNode previousNode = null;
    List<RoadNode> listOfRoadNodes = new ArrayList<>();

    double lon;
    double lat;

    public abstract int getRequestCode();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
        }


        //GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        //addMarker(geoPoint);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OpenStreetMapTileProviderConstants.setCachePath(this.getFilesDir().getAbsolutePath());
        OpenStreetMapTileProviderConstants.setCacheSizes(1000L, 900L);
        OpenStreetMapTileProviderConstants.setOfflineMapsPath("/storage/extSdCard/osmdroid");
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);


        //Introduction
        setContentView(R.layout.activity_nav);

        MAPBOXSATELLITELABELLED = new MapBoxTileSource("MapBoxSatelliteLabelled", 1, 19, 256, ".png");
        ((MapBoxTileSource) MAPBOXSATELLITELABELLED).retrieveAccessToken(this);
        ((MapBoxTileSource) MAPBOXSATELLITELABELLED).retrieveMapBoxMapId(this);
        TileSourceFactory.addTileSource(MAPBOXSATELLITELABELLED);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        map = (MapView) findViewById(R.id.map);
        icon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
        infoWindow = new MarkerInfoWindow(R.layout.bubble, map);

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();

        mapController.setZoom(16);
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        map.setVisibility(View.INVISIBLE);

        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(map);
        map.getOverlays().add(scaleBarOverlay);

        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        context = this;
        routeInfo = (TextView) findViewById(R.id.routeInfo);

        setStartPoint(location);

        Intent intent = getIntent();
        lon = intent.getDoubleExtra("lon", 0.0);
        lat = intent.getDoubleExtra("lat", 0.0);
        if (lon == 0.0 && lat == 0.0) {
            Intent goToIntent = new Intent(this, SearchActivity.class);
            startActivityForResult(goToIntent, getRequestCode());
        } else {
            onAddressRecived(lon, lat);
        }
    }

    private void setStartPoint(Location location) {
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setIcon(getResources().getDrawable(R.drawable.start));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Start point");
        startMarker.setDraggable(true);
        map.getOverlays().add(startMarker);
        waypoints.add(startPoint);

        System.out.println("startPoint:" + location.getLatitude() + " / " + location.getLongitude());
    }

    public void onAddressRecived(double longitude, double latitude) {

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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu4, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.night:
            {
                map.getOverlayManager().getTilesOverlay().setColorFilter(TilesOverlay.INVERT_COLORS);
                map.invalidate();
                break;
            }
            case R.id.satelita:
            {
                if (!(map.getTileProvider() instanceof MapTileProviderBasic)){
                    MapTileProviderBasic bitmapProvider = new MapTileProviderBasic(this);
                    map.setTileProvider(bitmapProvider);
                }
                map.setTileSource(MAPBOXSATELLITELABELLED);
                map.getOverlayManager().getTilesOverlay().setColorFilter(null);
                map.invalidate();
                break;
            }
            case R.id.historia:
            {
                Intent goToIntent = new Intent(this, HistoryActivity.class);
                startActivity(goToIntent);
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    protected void fillNodeRoadInfo(int roadStep, RoadNode node) {
        if(roadStep == 0) {
            nextNode = node;
        }
        if(roadStep == 1) {
            previousNode = node;
        }
        listOfRoadNodes.add(node);
        String instructions = (node.mInstructions == null ? "" : node.mInstructions);
        Marker nodeMarker = new Marker(map);
        nodeMarker.setTitle(getString(R.string.step) + " " + (roadStep + 1));
        nodeMarker.setSnippet(instructions);
        nodeMarker.setSubDescription(RoadDescription.getLenAndDurAsString(context, node.mLength, node.mDuration));
        nodeMarker.setPosition(node.mLocation);
        nodeMarker.setIcon(icon);
        nodeMarker.setInfoWindow(infoWindow);

        Drawable directionIcon = RoadDescription.chooseIconForManeuver(node.mManeuverType, context);
        nodeMarker.setImage(directionIcon);
        mRoadNodeMarkers.add(nodeMarker);
    }

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

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
        if(requestCode == getRequestCode()) {
            if (resultCode == RESULT_OK) {
                lon = data.getDoubleExtra("lon", 0.0);
                lat = data.getDoubleExtra("lat", 0.0);
                onAddressRecived(lon, lat);
            }
        }
    }

    private void addMarker(GeoPoint center) {
        if(positionMarker != null) {
            map.getOverlays().remove(positionMarker);
        }
        positionMarker = new Marker(map);
        positionMarker.setPosition(center);
        positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
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
        if(location != null) {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            addMarker(geoPoint);
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

        if(dLon < dMin && dLat < dMin) {
            return true;
        }
        return false;
    }
}
