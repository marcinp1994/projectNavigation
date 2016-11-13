package com.example.marcin.osmtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.bonuspack.routing.RoadLeg;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.bonuspack.utils.PolylineEncoder;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcin on 11.11.2016.
 * Klasa pobiera droge pomiedzy dwona wybranymi punktami. Wykorzystuje OSRM czyli ,
 * open-source darmowy routing-service który bazuje na danych z OpenStreetMap.
 * It requests by default the OSRM demo site.
 */
 class RoutingByOSRM {

    private static final String HTTP_OSRM_ROUTING_URL = "http://router.project-osrm.org/route/v1/driving/";
    private static final String ResponseCodeOK = "Ok";
    private String allServiceUrl;
    private String userAgent;

     RoutingByOSRM(Context context)
    {
        allServiceUrl = HTTP_OSRM_ROUTING_URL;
        userAgent = LoggerHelper.DEFAULT_USER_AGENT;
    }


    @SuppressLint("UseSparseArrays")
    private static final HashMap<Integer, String> DIRECTIONS;
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


    private String getHTTPUrl(ArrayList<GeoPoint> waypoints, boolean alternate)
    {
        StringBuffer httpURLString = new StringBuffer(allServiceUrl);
        int i=0;
        for(GeoPoint point : waypoints)
        {
            if(i>0)
            {
                httpURLString.append(";");
            }
            i++;
            httpURLString.append(point.getLongitude()).append(",").append(point.getLatitude());
        }
        httpURLString.append("?alternatives=").append(alternate ? "true" : "false");
        httpURLString.append("&overview=full&steps=true");
        String options = "";
        httpURLString.append(options);
        return httpURLString.toString();
    }

    private String getJsonResponseAsString(String url, String userAgent)
    {
        return LoggerHelper.getResponseStringFromUrl(url,userAgent);
    }

    private int getManeuverTypeCodeForDirection(String direction)
    {
        if(direction.equals("end of road") || direction.equals("use lane") || direction.equals("roundabout turn") || direction.equals("notification"))
        {
            return 0;
        }
        ManeuverType maneuverType = ManeuverType.valueOf(direction);
        int codeOfManeuverType = maneuverType.getValue();
        if (codeOfManeuverType == 0) {
            return 0;
        } else {
            return codeOfManeuverType;
        }
    }

    private ArrayList<RoadDescription> getRoads(ArrayList<GeoPoint> waypoints, boolean getAlternate)
    {
        String url = getHTTPUrl(waypoints, getAlternate);
        Log.d(LoggerHelper.LOG, "RoutingUrlOSRM" + url);
        String jsonResponseAsString = getJsonResponseAsString(url, userAgent);
        if (jsonResponseAsString == null)
        {
            Log.e(LoggerHelper.LOG, "RoutingByOSRM:getRoad: Getting response from http requset is failed!.");
            ArrayList<RoadDescription> roads = new ArrayList<>();
            roads.add(new RoadDescription(waypoints));
        }
        JsonParser jsonParser = new JsonParser();
        JsonObject responseAsJSONObject = (JsonObject) jsonParser.parse(jsonResponseAsString);
        JsonElement responseStatus = responseAsJSONObject.get("code");
        if(responseStatus.getAsString().equals(ResponseCodeOK)==false)
        {
            Log.e(LoggerHelper.LOG,"RoutingByOSRM.getRoad: error=" + responseStatus);
            ArrayList<RoadDescription> roads = new ArrayList<>();
            roads.add(new RoadDescription(waypoints));
            if ("NoRoute".equals(responseStatus.getAsString()))
            {
                roads.get(0).roadStatus = ResponseStatus.INVALID.getValue();
            }
            return roads;
        }
        else
        {
            JsonArray jsonRoutesFromResponse = responseAsJSONObject.getAsJsonArray("routes");
            ArrayList<RoadDescription> roads = new ArrayList<>();
            for (int i=0; i<jsonRoutesFromResponse.size(); i++)
            {
                RoadDescription road = new RoadDescription();
                roads.add(i,road);
                road.roadStatus = ResponseStatus.OK.getValue();
                JsonObject jRoute = jsonRoutesFromResponse.get(i).getAsJsonObject();
                String route_geometry = jRoute.get("geometry").getAsString();
                road.routeHigh = PolylineEncoder.decode(route_geometry, 10, false);
                road.boundingBox = BoundingBox.fromGeoPoints(road.routeHigh);
                road.totalLengthOfRoad = jRoute.get("distance").getAsDouble() / 1000.0;
                road.totalDurationOfRoad = jRoute.get("duration").getAsDouble();
                JsonArray jLegs = jRoute.getAsJsonArray("legs");
                for (int l=0; l<jLegs.size(); l++) {
                    JsonObject jLeg = jLegs.get(l).getAsJsonObject();
                    RoadLeg leg = new RoadLeg();
                    road.legs.add(leg);
                    leg.mLength = jLeg.get("distance").getAsDouble();
                    leg.mDuration = jLeg.get("duration").getAsDouble();
                    JsonArray jSteps = jLeg.getAsJsonArray("steps");
                    RoadNode lastNode = null;
                    String lastRoadName = "";
                    for (int s=0; s<jSteps.size(); s++) {
                        JsonObject jStep = jSteps.get(s).getAsJsonObject();
                        RoadNode node = new RoadNode();
                        node.mLength = jStep.get("distance").getAsDouble() / 1000.0;
                        node.mDuration = jStep.get("duration").getAsDouble();
                        JsonObject jStepManeuver = jStep.getAsJsonObject("maneuver");
                        JsonArray jLocation = jStepManeuver.getAsJsonArray("location");
                        node.mLocation = new GeoPoint(jLocation.get(1).getAsDouble(), jLocation.get(0).getAsDouble());
                        String direction = jStepManeuver.get("type").getAsString();
                        switch (direction) {
                            case "new name":
                                direction = direction.replaceAll("\\s+","");
                                break;
                            case "continue":
                                direction = "turnstraight";
                                break;
                            case "turn":
                            case "ramp":
                            case "merge":
                                String modifier = jStepManeuver.get("modifier").getAsString();
                                direction = direction + modifier;
                                direction = direction.replaceAll("\\s+","");
                                break;
                            case "roundabout": {
                                int exit = jStepManeuver.get("exit").getAsInt();
                                direction = direction + exit;
                                break;
                            }
                            case "rotary": {
                                int exit = jStepManeuver.get("exit").getAsInt();
                                direction = "roundabout" + exit;
                                break;
                            }
                            case "fork":
                            {
                                String modifier2 = jStepManeuver.get("modifier").getAsString();
                                direction = "turn" + modifier2;
                                direction = direction.replaceAll("\\s+","");
                                break;
                            }
                            case "off ramp":
                            case "on ramp":
                            {
                                String modifier3 = jStepManeuver.get("modifier").getAsString();
                                direction = "turn" + modifier3;
                                direction = direction.replaceAll("\\s+","");
                                break;
                            }
                        }
                        node.mManeuverType = getManeuverTypeCodeForDirection(direction);
                        String roadName = jStep.get("name").getAsString();
                        if(roadName == null)
                        {
                            roadName = "";
                        }
                        node.mInstructions = getDirectionInstruction(node.mManeuverType, roadName);
                        if (lastNode != null && node.mManeuverType == 2 && lastRoadName.equals(roadName)) {
                            lastNode.mDuration += node.mDuration;
                            lastNode.mLength += node.mLength;
                        } else {
                            road.allTurningPointsOfRoadPoints.add(node);
                            lastNode = node;
                            lastRoadName = roadName;
                        }
                    }
                }
            }
            Log.d(LoggerHelper.LOG, "RoutingByOSRM.getRoads - finished");
            return roads;
        }
    }

     RoadDescription getRoad(ArrayList<GeoPoint> waypoints) {
        ArrayList<RoadDescription> roads = getRoads(waypoints, false);
        return roads.get(0);
    }


    private String getDirectionInstruction(int manewr, String nameOfRoad){
        String direction = DIRECTIONS.get(manewr);
        if (direction == null)
        {
            return null;
        }
        String directionInstruction = null;
        if (nameOfRoad.equals(""))
        {
            directionInstruction = direction;
        }
        else
        {
            directionInstruction = direction + " w " + nameOfRoad;
        }
        return directionInstruction;
    }
}
