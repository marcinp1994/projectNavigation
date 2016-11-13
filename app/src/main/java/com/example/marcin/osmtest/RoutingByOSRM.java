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
                JsonObject routeAsJsonObject = jsonRoutesFromResponse.get(i).getAsJsonObject();
                String geometryFromRoute = routeAsJsonObject.get("geometry").getAsString();
                road.routeHigh = PolylineEncoder.decode(geometryFromRoute, 10, false);
                road.boundingBox = BoundingBox.fromGeoPoints(road.routeHigh);
                road.totalLengthOfRoad = routeAsJsonObject.get("distance").getAsDouble() / 1000.0;
                road.totalDurationOfRoad = routeAsJsonObject.get("duration").getAsDouble();
                JsonArray legsAsJsonArray = routeAsJsonObject.getAsJsonArray("legs");
                for (int l=0; l<legsAsJsonArray.size(); l++) {
                    JsonObject legAsJsonObject = legsAsJsonArray.get(l).getAsJsonObject();
                    RoadLeg leg = new RoadLeg();
                    road.legs.add(leg);
                    leg.mLength = legAsJsonObject.get("distance").getAsDouble();
                    leg.mDuration = legAsJsonObject.get("duration").getAsDouble();
                    JsonArray stepsAsJsonArray = legAsJsonObject.getAsJsonArray("steps");
                    RoadNode lastNode = null;
                    String lastRoadName = "";
                    for (int s=0; s<stepsAsJsonArray.size(); s++) {
                        JsonObject stepAsJsonObject = stepsAsJsonArray.get(s).getAsJsonObject();
                        RoadNode node = new RoadNode();
                        node.mLength = stepAsJsonObject.get("distance").getAsDouble() / 1000.0;
                        node.mDuration = stepAsJsonObject.get("duration").getAsDouble();
                        JsonObject maneuverForEachStep = stepAsJsonObject.getAsJsonObject("maneuver");
                        JsonArray nodeLocationAsJsonArray = maneuverForEachStep.getAsJsonArray("location");
                        node.mLocation = new GeoPoint(nodeLocationAsJsonArray.get(1).getAsDouble(), nodeLocationAsJsonArray.get(0).getAsDouble());
                        String maneuver = maneuverForEachStep.get("type").getAsString();
                        switch (maneuver) {
                            case "new name":
                                maneuver = maneuver.replaceAll("\\s+","");
                                break;
                            case "continue":
                                maneuver = "turnstraight";
                                break;
                            case "turn":
                            case "ramp":
                            case "merge":
                                String modifier = maneuverForEachStep.get("modifier").getAsString();
                                maneuver = maneuver + modifier;
                                maneuver = maneuver.replaceAll("\\s+","");
                                break;
                            case "roundabout": {
                                int exit = maneuverForEachStep.get("exit").getAsInt();
                                maneuver = maneuver + exit;
                                break;
                            }
                            case "rotary": {
                                int exit = maneuverForEachStep.get("exit").getAsInt();
                                maneuver = "roundabout" + exit;
                                break;
                            }
                            case "fork":
                            {
                                String modifier2 = maneuverForEachStep.get("modifier").getAsString();
                                maneuver = "turn" + modifier2;
                                maneuver = maneuver.replaceAll("\\s+","");
                                break;
                            }
                            case "off ramp":
                            case "on ramp":
                            {
                                String modifier3 = maneuverForEachStep.get("modifier").getAsString();
                                maneuver = "turn" + modifier3;
                                maneuver = maneuver.replaceAll("\\s+","");
                                break;
                            }
                        }
                        node.mManeuverType = getManeuverTypeCodeForDirection(maneuver);
                        String roadName = stepAsJsonObject.get("name").getAsString();
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

     RoadDescription getRoad(ArrayList<GeoPoint> waypoints)
     {
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
