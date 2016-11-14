package com.example.marcin.osmtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.location.LocationListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class MainActivity extends Activity   {
    private MapView mMapView;
    private MapController mMapController;
    MapView map;
    private LocationManager locationManager;
    private LocationListener  listener;
    private String mProviderName;
    protected FolderOverlay mRoadNodeMarkers;
    EditText editText;
    Button button;
    double a,b;
    ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
    RoutingByOSRM roadManager;
    Context context;
    IMapController mapController;


    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 12;

    /* Position */
    private static final int MINIMUM_TIME = 10000;  // 10s
    private static final int MINIMUM_DISTANCE = 50; // 50m

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        OpenStreetMapTileProviderConstants.setCachePath(this.getFilesDir().getAbsolutePath());
        OpenStreetMapTileProviderConstants.setCacheSizes(1000L, 900L);
        OpenStreetMapTileProviderConstants.setOfflineMapsPath("/storage/extSdCard/osmdroid");
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);

        //Introduction
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(52.22, 21.01);
         mapController = map.getController();
        mapController.setZoom(10);
        mapController.setCenter(startPoint);
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        map.invalidate();



        final Button navButton = (Button)findViewById(R.id.nav);
        final Button carButton = (Button)findViewById(R.id.car);
        final Button bikeButton = (Button) findViewById(R.id.bike);

        navButton.setVisibility(View.VISIBLE);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carButton.getVisibility() == View.INVISIBLE && bikeButton.getVisibility() == View.INVISIBLE)
                {
                    carButton.setVisibility(View.VISIBLE);
                    bikeButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    carButton.setVisibility(View.INVISIBLE);
                    bikeButton.setVisibility(View.INVISIBLE);
                }
                //when play is clicked show stop button and hide play button

            }
        });



        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(map);
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();
        map.getOverlayManager().add(myLocationNewOverlay);

//        GeoNamesPOIProvider poiProvider = new GeoNamesPOIProvider("OsmNavigator/1.0");
//        BoundingBox bb = map.getBoundingBox();
//        ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, 30, 20.0);
//        FolderOverlay poiMarkers = new FolderOverlay(this);
//        map.getOverlays().add(poiMarkers);
//        Drawable poiIcon = getResources().getDrawable(R.drawable.marker_poi_default);
//        for (POI poi:pois){
//            Marker poiMarker = new Marker(map);
//            poiMarker.setTitle(poi.mType);
//            poiMarker.setSnippet(poi.mDescription);
//            poiMarker.setPosition(poi.mLocation);
//            poiMarker.setIcon(poiIcon);
//            if (poi.mThumbnail != null){
//                poiMarker.setImage(new BitmapDrawable(poi.mThumbnail));
//            }
//            poiMarkers.add(poiMarker);
//        }

    }

    public void showPositionOnClick(View view)
    {
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(map);
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();
        map.getOverlayManager().add(myLocationNewOverlay);
    }

    public void carOnClick(View view)
    {
        Intent goToIntent = new Intent(this, NavCarActivity.class);
        startActivity(goToIntent);
    }
    public void addMarker(GeoPoint center) {
        Marker marker = new Marker(map);
        marker.setPosition(center);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(getResources().getDrawable(R.drawable.navigation));
        map.getOverlays().clear();
        map.getOverlays().add(marker);
        map.invalidate();
    }

}