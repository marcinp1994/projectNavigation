package com.example.marcin.osmtest;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;

import org.osmdroid.bonuspack.routing.GraphHopperRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

public class NavBikeActivity extends NavActivity
{
    public int getRequestCode() {
        return 1;
    }

    public void onAddressRecived(double latitude, double longitude) {
        GeoPoint endPoint = new GeoPoint(latitude, longitude);
        Marker endMarker = new Marker(map);
        endMarker.setPosition(endPoint);
        endMarker.setIcon(getResources().getDrawable(R.drawable.end));
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        endMarker.setTitle("End point");
        waypoints.add(endPoint);
        endMarker.setDraggable(true);
        map.setVisibility(View.VISIBLE);
        map.invalidate();
        map.getOverlays().add(endMarker);

        new Thread(new Runnable() {
            public synchronized void run() {
                roadManagerForMapQuest = new GraphHopperRoadManager("AMFmC5P8s958tcjfFRJmefNboJ5H0HN6PLFyvdm3",false);
                roadManagerForMapQuest.addRequestOption("locale=pl");
                roadManagerForMapQuest.addRequestOption("vehicle=bike");
                road = roadManagerForMapQuest.getRoad(waypoints);
                lengthOfRoad = road.mLength;
                duration = road.mDuration;

               runOnUiThread(new Runnable() {
                    public synchronized void run() {
                        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.CYAN, 10);

                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.routeView);
                        linearLayout.setVisibility(View.VISIBLE);

                        map.getOverlays().add(roadOverlay);
                        roadPolyline = roadOverlay;
                        map.invalidate();
                        mRoadNodeMarkers = new FolderOverlay();
                        mRoadNodeMarkers.setName("Road Steps");
                        map.getOverlays().add(mRoadNodeMarkers);
                        mRoadNodeMarkers.getItems().clear();
                        int n = road.mNodes.size();
                        System.out.println(n);
                        for (int i = 0; i < n; i++) {
                            RoadNode node;
                            node = road.mNodes.get(i);
                            fillNodeRoadInfo(i, node);
                        }
                        routeInfo.setText(RoadDescription.getLenAndDurAsString(context, lengthOfRoad, duration));
                        map.getOverlays().add(mRoadNodeMarkers);
                        map.invalidate();

                        startLocationListener();
                    }
                });
            }
        }).start();
    }

}

