package com.example.marcin.osmtest.location;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.marcin.osmtest.R;
import com.example.marcin.osmtest.routing.RoadDescription;
import com.example.marcin.osmtest.routing.RoutingByOSRM;

import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import static com.example.marcin.osmtest.activity.HomeActivity.getOsrmOrMapquestOrGraphooper;

public class NavCarActivity extends NavActivity {
    RoutingByOSRM roadManagerForOSRM;
    RoadDescription road2 = null;

    @Override
    public int getRequestCode() {
        return 2;
    }

    @Override
    public void onAddressRecived(double latitude, double longitude) {
        String osrmOrMapQuest = getOsrmOrMapquestOrGraphooper();
        if (osrmOrMapQuest.equals("OSRM")) {
            roadManagerForOSRM = new RoutingByOSRM(this);
        } else if (osrmOrMapQuest.equals("MAP_QUEST")) {
            roadManagerForMapQuest = new MapQuestRoadManager(keyForMapQuest);
        } else {
            roadManagerForMapQuest = new GraphHopperRoadManager("0eb4be66-eda0-468e-a5e2-bcdeea1d1c98", false);
        }

        endPoint = new GeoPoint(latitude, longitude);
        Marker endMarker = new Marker(map);
        endMarker.setPosition(endPoint);
        endMarker.setIcon(getResources().getDrawable(R.drawable.end));
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endMarker.setTitle("End point");
        waypoints.add(endPoint);
        endMarker.setDraggable(true);
        map.setVisibility(View.VISIBLE);
        map.getOverlays().add(endMarker);

        new Thread(new Runnable() {
            public synchronized void run() {

                Polyline roadOverlay;

                if (roadManagerForOSRM != null) {
                    road2 = roadManagerForOSRM.getRoad(waypoints);
                    lengthOfRoad = roadManagerForOSRM.getRoad(waypoints).totalLengthOfRoad;
                    duration = roadManagerForOSRM.getRoad(waypoints).totalDurationOfRoad;
                    if (road2.roadStatus != Road.STATUS_OK)
                        Toast.makeText(context, "Error when loading the road2 - status=" + road2.roadStatus, Toast.LENGTH_SHORT).show();

                    roadOverlay = RoadDescription.buildRoadOverlay(road2, Color.BLUE, 10);
                } else {
                    road = roadManagerForMapQuest.getRoad(waypoints);
                    lengthOfRoad = roadManagerForMapQuest.getRoad(waypoints).mLength;
                    duration = roadManagerForMapQuest.getRoad(waypoints).mDuration;
                    if (road.mStatus != Road.STATUS_OK)
                        Toast.makeText(context, "Error when loading the road2 - status=" + road.mStatus, Toast.LENGTH_SHORT).show();

                    roadOverlay = RoadManager.buildRoadOverlay(road, Color.BLUE, 10);
                }

                map.getOverlays().add(roadOverlay);
                roadPolyline = roadOverlay;

                runOnUiThread(new Runnable() {
                    public void run() {
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.routeView);
                        linearLayout.setVisibility(View.VISIBLE);
                        mRoadNodeMarkers = new FolderOverlay();
                        mRoadNodeMarkers.setName("Road Steps");
                        map.getOverlays().add(mRoadNodeMarkers);
                        mRoadNodeMarkers.getItems().clear();
                        int n;
                        if (roadManagerForOSRM != null) {
                            assert road2 != null;
                            n = road2.allTurningPointsOfRoadPoints.size();
                        } else {
                            assert road != null;
                            n = road.mNodes.size();
                        }
                        for (int i = 0; i < n; i++)
                        {
                            RoadNode node;
                            if (roadManagerForOSRM != null) {
                                assert road2 != null;
                                node = road2.allTurningPointsOfRoadPoints.get(i);
                            } else {
                                assert road != null;
                                node = road.mNodes.get(i);
                            }
                           fillNodeRoadInfo(i,node);
                        }
                        routeInfo.setText(RoadDescription.getLenAndDurAsString(context, lengthOfRoad, duration, true));
                        map.getOverlays().add(mRoadNodeMarkers);
                        map.invalidate();

//                        startLocationListener();
                    }
                });
            }
        }).start();
    }

}