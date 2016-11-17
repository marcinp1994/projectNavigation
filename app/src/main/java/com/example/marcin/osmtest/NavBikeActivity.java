package com.example.marcin.osmtest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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

public class NavBikeActivity extends Activity {
    public static final String keyForMapQuest = "ETefQk4KAr64RQryy3gD1tbwDZsqA0IX";
    static MapView map;
    protected FolderOverlay mRoadNodeMarkers;
    EditText editText;
    Button button;
    ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
    static RoadManager roadManagerForMapQuest;
    Context context;
    IMapController mapController;
    ListView listView;
    TextView routeInfo;
    static double lengthOfRoad = 0;
    static double duration = 0;
    int n = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static Address getAdres() {
        return adres;
    }

    public static void setAdres(Address adres) {
        NavBikeActivity.adres = adres;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static void setLongitude(double longitude) {
        NavBikeActivity.longitude = longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        NavBikeActivity.latitude = latitude;
    }

    public static double latitude;
    public static double longitude;
    public static Address adres = null;
    Road road = null;
    GeoPoint startPoint;

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                while (true) {
                    sleep(1000);
                    try
                    {
                        map.invalidate();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


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


        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setIcon(getResources().getDrawable(R.drawable.start));
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Start point");
        startMarker.setDraggable(true);
        map.getOverlays().add(startMarker);
        listView = (ListView) findViewById(android.R.id.list);

//        roadManagerForMapQuest = new MapQuestRoadManager(keyForMapQuest);
//        roadManagerForMapQuest.addRequestOption("routeType=bicycle");

        context = this;
        waypoints.add(startPoint);
        editText = (EditText) findViewById(R.id.destination);
        routeInfo = (TextView) findViewById(R.id.routeInfo);
        button = (Button) findViewById(R.id.buttonSearchDep);
        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        List<Address> addresses = null;
                        GeocoderNominatim geocoder = new GeocoderNominatim("navigationgps1");
                        try {
                            addresses = geocoder.getFromLocationName(editText.getText().toString(), 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Address adress = null;
                        if (addresses.size() == 0) {
                            Toast.makeText(context, "Bad address: " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            adress = addresses.get(0);
                            adres = adress;
                            latitude = adress.getLatitude();
                            longitude = adress.getLongitude();
                            AddressesDataSource datasource = new AddressesDataSource(context);
                            datasource.open();

                            StringBuilder sb = new StringBuilder();
                            String adressInfo;

                            if (adress.getMaxAddressLineIndex() == 0) {
                                adressInfo = adress.getCountryName() + ", " + adress.getSubAdminArea();
                                sb.append(adressInfo);
                            } else {
                                for (int a = 0; a < adress.getMaxAddressLineIndex() + 1; a++) {
                                    adressInfo = adress.getAddressLine(a);
                                    if (a == adress.getMaxAddressLineIndex()) {

                                    } else {
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

                            new Thread(new Runnable() {
                                public synchronized void run() {
                                    roadManagerForMapQuest = new MapQuestRoadManager(keyForMapQuest);
                                    roadManagerForMapQuest.addRequestOption("routeType=bicycle");

                                    try {
                                        road = roadManagerForMapQuest.getRoad(waypoints);
                                        map.invalidate();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    runOnUiThread(new Runnable() {
                                        public void run() {

                                            Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.CYAN, 10);

                                            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.routeView);
                                            linearLayout.setVisibility(View.VISIBLE);
                                            MarkerInfoWindow infoWindow1 = new MarkerInfoWindow(R.layout.destination_info, map);

                                            map.getOverlays().add(roadOverlay);
                                            map.invalidate();
                                            mRoadNodeMarkers = new FolderOverlay();
                                            mRoadNodeMarkers.setName("Road Steps");
                                            map.getOverlays().add(mRoadNodeMarkers);
                                            mRoadNodeMarkers.getItems().clear();
                                            Drawable icon = ResourcesCompat.getDrawable(getResources(), R.drawable.marker_node, null);
                                            n = road.mNodes.size();
                                            MarkerInfoWindow infoWindow = new MarkerInfoWindow(org.osmdroid.bonuspack.R.layout.bonuspack_bubble, map);
                                            for (int i = 0; i < n; i++) {
                                                RoadNode node;
                                                node = road.mNodes.get(i);
                                                String instructions = (node.mInstructions == null ? "" : node.mInstructions);
                                                Marker nodeMarker = new Marker(map);
                                                nodeMarker.setTitle(getString(R.string.step) + " " + (i + 1));
                                                nodeMarker.setSnippet(instructions);
                                                nodeMarker.setSubDescription(RoadDescription.getLenAndDurAsString(context, node.mLength, node.mDuration));
                                                nodeMarker.setPosition(node.mLocation);
                                                nodeMarker.setIcon(icon);
                                                nodeMarker.setInfoWindow(infoWindow);

                                                Drawable directionIcon = RoadDescription.chooseIconForManeuver(node.mManeuverType, context);
                                                nodeMarker.setImage(directionIcon);
                                                mRoadNodeMarkers.add(nodeMarker);
                                            }
                                            lengthOfRoad = road.mLength;
                                            duration = road.mDuration;
                                            routeInfo.setText(RoadDescription.getLenAndDurAsString(context, lengthOfRoad, duration));
                                            map.getOverlays().add(mRoadNodeMarkers);
                                            map.invalidate();
                                        }
                                    });
                                }

                            }).start();


//                            map.getOverlays().add(endMarker);
//
//                            double lengthOfRoad = 0;
//                            double duration = 0;
//
//                            road = roadManagerForMapQuest.getRoad(waypoints);
//                            lengthOfRoad = roadManagerForMapQuest.getRoad(waypoints).mLength;
//                            duration = roadManagerForMapQuest.getRoad(waypoints).mDuration;
//                            if (road.mStatus!= Road.STATUS_OK)
//                            {
//                                Toast.makeText(context, "Error when loading the road - status=" + road.mStatus, Toast.LENGTH_SHORT).show();
//                            }
//                            Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.CYAN, 10);


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
                    }
                });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void BtnTrackingModeOnClick(View view) {
        MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(map);
        myLocationNewOverlay.enableFollowLocation();
        myLocationNewOverlay.enableMyLocation();
        map.getOverlayManager().add(myLocationNewOverlay);
        mapController.setZoom(14);
        if (myLocationNewOverlay.getMyLocation() != null)
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("NavBike Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

