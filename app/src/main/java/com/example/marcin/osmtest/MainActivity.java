package com.example.marcin.osmtest;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        GeoPoint startPoint = new GeoPoint(50.0098, 19.9514);
        IMapController mapController = map.getController();
        mapController.setZoom(14);
        mapController.setCenter(startPoint);
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        
        //0. Using the Marker overlay
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setIcon(getResources().getDrawable(R.drawable.start));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Start point");
        // startMarker.setIcon(getResources().getDrawable(R.drawable.marker_kml_point).mutate());
        // startMarker.setImage(getResources().getDrawable(R.drawable.ic_launcher));
        //startMarker.setInfoWindow(new MarkerInfoWindow(R.layout.bonuspack_bubble_black, map));
        startMarker.setDraggable(true);
        map.getOverlays().add(startMarker);

        //1. "Hello, Routing World"
         roadManager = new RoutingByOSRM(this);
        //2. Playing with the RoadManager
        //roadManager roadManager = new MapQuestRoadManager("YOUR_API_KEY");
        //roadManager.addRequestOption("routeType=bicycle");
        context = this;
        waypoints.add(startPoint);
        editText   = (EditText)findViewById(R.id.destination);
        button = (Button)findViewById(R.id.buttonSearchDep);
        button.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        List<Address> addresses =null;
                        GeocoderNominatim geocoder = new GeocoderNominatim("navigationgps1");
                        try {
                           addresses= geocoder.getFromLocationName(editText.getText().toString(),1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address adress = addresses.get(0);
                        a = adress.getLatitude();
                        b = adress.getLongitude();
                        GeoPoint endPoint = new GeoPoint(a, b);
                        Marker endMarker = new Marker(map);
                        endMarker.setPosition(endPoint);
                        endMarker.setIcon(getResources().getDrawable(R.drawable.end));
                        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                        endMarker.setTitle("End point");
                        waypoints.add(endPoint);
                        endMarker.setDraggable(true);
                        map.getOverlays().add(endMarker);
                        RoadDescription road = roadManager.getRoad(waypoints);
                        double lengthOfRoad = roadManager.getRoad(waypoints).totalLengthOfRoad;
                        double duration = roadManager.getRoad(waypoints).totalDurationOfRoad;
                        System.out.println(lengthOfRoad + "__" + duration);
                        MarkerInfoWindow infoWindow1 = new MarkerInfoWindow(R.layout.destination_info, map);

                        if (road.roadStatus != Road.STATUS_OK)
                            Toast.makeText(context, "Error when loading the road - status=" + road.roadStatus, Toast.LENGTH_SHORT).show();

                        Polyline roadOverlay = RoadDescription.buildRoadOverlay(road, Color.BLUE, 10);
                        map.getOverlays().add(roadOverlay);
                        map.invalidate();
                        mRoadNodeMarkers = new FolderOverlay();
                        mRoadNodeMarkers.setName("Road Steps");
                        map.getOverlays().add(mRoadNodeMarkers);
                        mRoadNodeMarkers.getItems().clear();
                        Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
                        int n = road.allTurningPointsOfRoadPoints.size();
                        MarkerInfoWindow infoWindow = new MarkerInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map);
                        TypedArray iconIds = getResources().obtainTypedArray(R.array.direction_icons);
                        for (int i=0; i<n; i++){
                            RoadNode node = road.allTurningPointsOfRoadPoints.get(i);
                            String instructions = (node.mInstructions==null ? "" : node.mInstructions);
                            Marker nodeMarker = new Marker(map);
                            nodeMarker.setTitle(getString(R.string.step)+ " " + (i+1));
                            nodeMarker.setSnippet(instructions);
                            nodeMarker.setSubDescription(RoadDescription.getLenAndDurAsString(context, node.mLength, node.mDuration));
                            nodeMarker.setPosition(node.mLocation);
                            nodeMarker.setIcon(icon);
                            nodeMarker.setInfoWindow(infoWindow); //use a shared infowindow.
                            int iconId = iconIds.getResourceId(node.mManeuverType, R.drawable.empty);
                            if (iconId != R.drawable.empty){
                                Drawable image = ResourcesCompat.getDrawable(getResources(), iconId, null);
                                nodeMarker.setImage(image);
                            }
                            mRoadNodeMarkers.add(nodeMarker);
                        }
                        map.getOverlays().add(mRoadNodeMarkers);
                        iconIds.recycle();
                        map.invalidate();
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