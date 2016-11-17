package com.example.marcin.osmtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.marcin.osmtest.HomeActivity.getOsrmOrMapquest;

public class NavCarActivity extends Activity {
    private final String keyForMapQuest = "ETefQk4KAr64RQryy3gD1tbwDZsqA0IX";
    MapView map;
    protected FolderOverlay mRoadNodeMarkers;
    EditText editText;
    Button button;
    ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
    MapQuestRoadManager roadManagerForMapQuest;
    RoutingByOSRM roadManagerForOSRM;
    Context context;
    IMapController mapController;
    ListView listView;
    TextView routeInfo;
    public static double latitude;
    public static double longitude;
    public static Address adres;


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
        setContentView(R.layout.activity_nav_bike);
        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        GeoPoint startPoint = new GeoPoint(50.0098, 19.9514);
        mapController = map.getController();
        mapController.setZoom(14);
        mapController.setCenter(startPoint);
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        String osrmOrMapQuest = getOsrmOrMapquest();
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
        listView = (ListView) findViewById(android.R.id.list);

        if(osrmOrMapQuest.equals("OSRM"))
        {
            roadManagerForOSRM = new RoutingByOSRM(this);
        }
        else
        {
            roadManagerForMapQuest = new MapQuestRoadManager(keyForMapQuest);

        }
        context = this;
        waypoints.add(startPoint);
        editText   = (EditText)findViewById(R.id.destination);
        routeInfo = (TextView) findViewById(R.id.routeInfo);
        button = (Button)findViewById(R.id.buttonSearchDep);
        button.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        List<Address> addresses =null;
                        GeocoderNominatim geocoder = new GeocoderNominatim("navigationgps1");
                        try {
                            addresses= geocoder.getFromLocationName(editText.getText().toString(),1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Address adress = null;
                        if(addresses.size() == 0)
                        {
                            Toast.makeText(context, "Bad address: " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            adress = addresses.get(0);
                            adres = adress;
                            latitude = adress.getLatitude();
                            longitude = adress.getLongitude();
                            AddressesDataSource datasource = new AddressesDataSource(context);
                            datasource.open();

                            StringBuilder sb = new StringBuilder();
                            String adressInfo;

                            if(adress.getMaxAddressLineIndex() == 0)
                            {
                                adressInfo = adress.getCountryName() + ", " + adress.getSubAdminArea();
                                sb.append(adressInfo);
                            }
                            else
                            {
                                for(int a= 0 ; a< adress.getMaxAddressLineIndex()+1; a++ )
                                {
                                    adressInfo = adress.getAddressLine(a);
                                    if(a == adress.getMaxAddressLineIndex())
                                    {

                                    }
                                    else
                                    {
                                        adressInfo += ", ";
                                    }
                                    sb.append(adressInfo);
                                }
                            }

                            String addressName = sb.toString();
                            datasource.createDatabaseAddress(addressName, latitude, longitude);
                            GeoPoint endPoint = new GeoPoint(latitude, longitude);
                            Marker endMarker = new Marker(map);
                            endMarker.setPosition(endPoint);
                            endMarker.setIcon(getResources().getDrawable(R.drawable.end));
                            endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            endMarker.setTitle("End point");
                            waypoints.add(endPoint);
                            endMarker.setDraggable(true);
                            map.getOverlays().add(endMarker);
                            RoadDescription road = null;
                            Road road2 = null;
                            double lengthOfRoad = 0;
                            double duration = 0;
                            Polyline roadOverlay;
                            if(roadManagerForOSRM != null)
                            {
                                road = roadManagerForOSRM.getRoad(waypoints);
                                lengthOfRoad = roadManagerForOSRM.getRoad(waypoints).totalLengthOfRoad;
                                duration = roadManagerForOSRM.getRoad(waypoints).totalDurationOfRoad;
                                if (road.roadStatus!= Road.STATUS_OK)
                                    Toast.makeText(context, "Error when loading the road - status=" + road.roadStatus, Toast.LENGTH_SHORT).show();

                                roadOverlay = RoadDescription.buildRoadOverlay(road, Color.BLUE, 10);
                            }
                            else
                            {
                                road2 = roadManagerForMapQuest.getRoad(waypoints);
                                lengthOfRoad = roadManagerForMapQuest.getRoad(waypoints).mLength;
                                duration = roadManagerForMapQuest.getRoad(waypoints).mDuration;
                                if (road2.mStatus!= Road.STATUS_OK)
                                    Toast.makeText(context, "Error when loading the road - status=" + road2.mStatus, Toast.LENGTH_SHORT).show();

                                roadOverlay = RoadManager.buildRoadOverlay(road2, Color.BLUE, 10);
                            }

                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.routeView);
                            linearLayout.setVisibility(View.VISIBLE);
                            routeInfo.setText(RoadDescription.getLenAndDurAsString(context, lengthOfRoad,duration));
                            MarkerInfoWindow infoWindow1 = new MarkerInfoWindow(R.layout.destination_info, map);

                            map.getOverlays().add(roadOverlay);
                            map.invalidate();
                            mRoadNodeMarkers = new FolderOverlay();
                            mRoadNodeMarkers.setName("Road Steps");
                            map.getOverlays().add(mRoadNodeMarkers);
                            mRoadNodeMarkers.getItems().clear();
                            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
                            int n;
                            if(roadManagerForOSRM != null){
                                assert road != null;
                                n = road.allTurningPointsOfRoadPoints.size();
                            }
                            else
                            {
                                assert road2 != null;
                                n=road2.mNodes.size();
                            }
                            MarkerInfoWindow infoWindow = new MarkerInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map);
                            for (int i=0; i<n; i++){
                                RoadNode node;
                                if(roadManagerForOSRM != null)
                                {
                                    assert road != null;
                                    node = road.allTurningPointsOfRoadPoints.get(i);
                                }
                                else
                                {
                                    assert road2 != null;
                                    node = road2.mNodes.get(i);
                                }
                                String instructions = (node.mInstructions==null ? "" : node.mInstructions);
                                Marker nodeMarker = new Marker(map);
                                nodeMarker.setTitle(getString(R.string.step)+ " " + (i+1));
                                nodeMarker.setSnippet(instructions);
                                nodeMarker.setSubDescription(RoadDescription.getLenAndDurAsString(context, node.mLength, node.mDuration));
                                nodeMarker.setPosition(node.mLocation);
                                nodeMarker.setIcon(icon);
                                nodeMarker.setInfoWindow(infoWindow);
                                Drawable destinationIcon = RoadDescription.chooseIconForManeuver(node.mManeuverType, context);
                                nodeMarker.setImage(destinationIcon);
                                mRoadNodeMarkers.add(nodeMarker);
                            }
                            map.getOverlays().add(mRoadNodeMarkers);
                            map.invalidate();
                        }
                    }


                });

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

    public void BtnTrackingModeOnClick(View view)
    {
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(map);
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();
        map.getOverlayManager().add(myLocationNewOverlay);
        mapController.setZoom(14);
        if(myLocationNewOverlay.getMyLocation() != null)
            mapController.setCenter(myLocationNewOverlay.getMyLocation());

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