package com.example.marcin.osmtest;

import android.content.Context;

import org.osmdroid.bonuspack.routing.RoadLeg;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

/**
 * Created by Marcin on 11.11.2016.
 * Klasa implementujaca interfejs IRoadDescription
 * ktora zawiera w sobie informacje drogowe na temat
 * dlugosci drogi, czasu przejazdu
 * */
 class RoadDescription
{
    int roadStatus;
    double totalLengthOfRoad;
    double totalDurationOfRoad;
    ArrayList<RoadNode> allTurningPointsOfRoadPoints;
    ArrayList<RoadLeg> legs;
    ArrayList<GeoPoint> routeHigh;
    ArrayList<GeoPoint> routeLow;
    BoundingBox boundingBox;

    private void initialize(){
        roadStatus = ResponseStatus.INVALID.getValue();
        totalLengthOfRoad = 0.0;
        totalDurationOfRoad = 0.0;
        allTurningPointsOfRoadPoints = new ArrayList<>();
        routeHigh = new ArrayList<>();
        routeLow = null;
        legs = new ArrayList<>();
        boundingBox = null;
    }

    /**
     * Defaultowy konstruktoe w razie gdyby normalne ladowanie drog zawiodlo
     */
     RoadDescription()
    {
        roadStatus = ResponseStatus.INVALID.getValue();
        totalLengthOfRoad = 0.0;
        totalDurationOfRoad = 0.0;
        allTurningPointsOfRoadPoints = new ArrayList<>();
        routeHigh = new ArrayList<>();
        routeLow = null;
        legs = new ArrayList<>();
        boundingBox = null;
    }

     RoadDescription(ArrayList<GeoPoint> listOfWaypoints)
    {
        initialize();
        int numberOfWaypoints = listOfWaypoints.size();
        for(GeoPoint point : listOfWaypoints)
        {
            routeHigh.add(point);
        }
        do
        {
            RoadLeg leg = new RoadLeg();
            legs.add(leg);
        }while (numberOfWaypoints == numberOfWaypoints-1);

        boundingBox = BoundingBox.fromGeoPoints(routeHigh);
        roadStatus = ResponseStatus.TECHNICAL.getValue();
    }

     static String getLenAndDurAsString(Context context, double length, double duration) {
        String lengthAndDurationAsString = null;
        if (length >= 100.0)
        {
            lengthAndDurationAsString = "Długość: " + length + "km";
        }
        else if (length >= 1.0)
        {
            lengthAndDurationAsString = "Długość: " + Math.round(length*10)/10.0 + "km";
        }
        else
        {
            lengthAndDurationAsString = "Długość: " + length*1000 + "m";
        }
        int totalTimeInSeconds = (int)duration;
        int hours = totalTimeInSeconds / 3600;
        int minutes = (totalTimeInSeconds / 60) - (hours*60);
        int seconds = (totalTimeInSeconds % 60);
        lengthAndDurationAsString = lengthAndDurationAsString + "   Czas: ";
        if (hours != 0)
        {
            lengthAndDurationAsString += hours + "h ";
        }
        if (minutes != 0)
        {
            lengthAndDurationAsString += minutes + "min ";
        }
        if(seconds != 0)
        {
            lengthAndDurationAsString += seconds + "s ";
        }

        return lengthAndDurationAsString;
    }

     static Polyline buildRoadOverlay(RoadDescription road, int color, float width){
        Polyline roadOverlay = new Polyline();
        roadOverlay.setColor(color);
        roadOverlay.setWidth(width);
        if (road != null) {
            ArrayList<GeoPoint> polyline = road.routeHigh;
            roadOverlay.setPoints(polyline);
        }
        return roadOverlay;
    }


}
