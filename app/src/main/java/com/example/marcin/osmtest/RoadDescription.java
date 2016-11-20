package com.example.marcin.osmtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import org.osmdroid.bonuspack.routing.RoadLeg;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.HashMap;

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

    String roadOption;

    @SuppressLint("UseSparseArrays")
    static final HashMap<Integer, String> DIRECTIONS;
    static {
        DIRECTIONS = new HashMap<>();
        DIRECTIONS.put(1, "Kontynuuj jazdę");
        DIRECTIONS.put(2, "Jedź");
        DIRECTIONS.put(3, "Skręć lekko w lewo");
        DIRECTIONS.put(4, "Skręć w lewo");
        DIRECTIONS.put(5, "Skręć ostro w lewo");
        DIRECTIONS.put(6, "Skręc lekko w prawo");
        DIRECTIONS.put(7, "Skręć w prawo");
        DIRECTIONS.put(8, "Skręć ostro w prawo");
        DIRECTIONS.put(12, "Zawróc");
        DIRECTIONS.put(17, "Wybierz lewy zjazd");
        DIRECTIONS.put(18, "Wybierz prawy zjazd");
        DIRECTIONS.put(19, "Wybierz drogę na wprost");
        DIRECTIONS.put(24, "Dotarłeś do punktu");
        DIRECTIONS.put(27, "Wjedź na rondo i opuść je pierwszym zjazdem");
        DIRECTIONS.put(28, "Wjedź na rondo i opuść je drugim zjazdem");
        DIRECTIONS.put(29, "Wjedź na rondo i opuść je trzecim zjazdem");
        DIRECTIONS.put(30, "Wjedź na rondo i opuść je czwartym zjazdem");
        DIRECTIONS.put(31, "Wjedź na rondo i opuść je piątym zjazdem");
        DIRECTIONS.put(32, "Wjedź na rondo i opuść je szóstym zjazdem");
        DIRECTIONS.put(33, "Wjedź na rondo i opuść je siódmym zjazdem");
        DIRECTIONS.put(34, "Wjedź na rondo i opuść je osmym zjazdem");
    }


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
            lengthAndDurationAsString = "Długość: " + Math.round(length) + "km";
        }
        else if (length >= 1.0)
        {
            lengthAndDurationAsString = "Długość: " + Math.round(length*10)/10.0 + "km";
        }
        else
        {
            lengthAndDurationAsString = "Długość: " + Math.round(length*1000) + "m";
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


    public String getRoadOption() {
        return roadOption;
    }

    public void setRoadOption(String roadOption) {
        this.roadOption = roadOption;
    }

    public static Drawable chooseIconForManeuver(int manewr, Context context)
    {
        Drawable directionIcon = null;
        switch (manewr)
        {
            case 0:
            {
                directionIcon =  context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 1:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.straight);
                break;
            }
            case 2:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.straight);
                break;
            }
            case 3:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightleft);
                break;
            }
            case 4:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.turnleft);
                break;
            }
            case 5:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.sharpleft);
                break;
            }
            case 6:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightright);
                break;
            }
            case 7:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.turnright);
                break;
            }
            case 8:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.sharpright);
                break;
            }
            case 9:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightleft);
                break;
            }
            case 10:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightright);
                break;
            }
            case 11:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.straight);
                break;
            }
            case 12:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.uturn);
                break;
            }
            case 13:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.uturn);
                break;
            }
            case 14:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.uturn);
                break;
            }
            case 15:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightleft);
                break;
            }
            case 16:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightright);
                break;
            }
            case 17:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightleft);
                break;
            }
            case 18:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightright);
                break;
            }
            case 19:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.slightright);
                break;
            }
            case 20:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 21:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 22:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 23:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 24:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.arrived);
                break;
            }
            case 25:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.arrived);
                break;
            }
            case 26:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.arrived);
                break;
            }
            case 27:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 28:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 29:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 30:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 31:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 32:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 33:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 34:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.roundabout);
                break;
            }
            case 35:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 36:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 37:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 38:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }
            case 39:
            {
                directionIcon = context.getResources().getDrawable(R.drawable.empty);
                break;
            }

        }
        return directionIcon;
    }


}
