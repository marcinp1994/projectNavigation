package com.example.marcin.osmtest;

import android.location.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Marcin on 20.11.2016.
 */

public class AddressFromName {

    public static final String SERVICE_URL = "http://nominatim.openstreetmap.org/";


    private String buildHTTPURL(String location, int numberOfResults, String keyForMapQuest)
    {
        StringBuffer httpURLString = new StringBuffer(SERVICE_URL);
        int i=0;
        httpURLString.append("search?");
        httpURLString.append("key=" + keyForMapQuest + "&");
        httpURLString.append("format=json" + "&addressdetails=1" + "&limit=" + numberOfResults + "&q=" + URLEncoder.encode(location));
        return httpURLString.toString();
    }



    public ArrayList<Address> getFromLocationName(String locationName, int numberOfResults, String keyForMapQuest)
    {
        String httpUrl = buildHTTPURL(locationName, numberOfResults,keyForMapQuest);
        String responseAsString = ConnectionHelper.getResponseStringFromUrl(httpUrl,"navigationGPSv1");
        ArrayList<Address> listOfAddresses = null;
        if(responseAsString == null)
        {
            return null;
        }
        
        else
        {
            try {
                JSONArray jsonResponse = new JSONArray(responseAsString);
                listOfAddresses = new ArrayList<>(jsonResponse.length());
                for( int i=0; i<jsonResponse.length(); i++)
                {
                    JSONObject jsonAddress = jsonResponse.getJSONObject(i);
                    
                    Address address = new Address(Locale.getDefault());
                    
                    if(jsonAddress.has("lat") == false || jsonAddress.has("lon") == false || jsonAddress.has("address") == false)
                    {
                        return null;
                    }
                    else
                    {
                        address.setLatitude(jsonAddress.getDouble("lat"));
                        address.setLongitude(jsonAddress.getDouble("lon"));
                        JSONObject jAddress = jsonAddress.getJSONObject("address");

                        int index = 0;
                        if (jAddress.has("road")){
                            address.setAddressLine(index++, jAddress.getString("road"));
                            address.setThoroughfare(jAddress.getString("road"));
                        }
                        if (jAddress.has("suburb")){
                            address.setSubLocality(jAddress.getString("suburb"));
                        }
                        if (jAddress.has("postcode")){
                            address.setAddressLine(index++, jAddress.getString("postcode"));
                            address.setPostalCode(jAddress.getString("postcode"));
                        }

                        if (jAddress.has("city")){
                            address.setAddressLine(index++, jAddress.getString("city"));
                            address.setLocality(jAddress.getString("city"));
                        } else if (jAddress.has("town")){
                            address.setAddressLine(index++, jAddress.getString("town"));
                            address.setLocality(jAddress.getString("town"));
                        } else if (jAddress.has("village")){
                            address.setAddressLine(index++, jAddress.getString("village"));
                            address.setLocality(jAddress.getString("village"));
                        }
                        
                        if (jAddress.has("country")){
                            address.setAddressLine(index++, jAddress.getString("country"));
                            address.setCountryName(jAddress.getString("country"));
                        }
                        if (jAddress.has("country_code"))
                            address.setCountryCode(jAddress.getString("country_code"));
                        
                        listOfAddresses.add(address);
                    }
                }

            } catch (JSONException e) 
            {
                e.printStackTrace();
            }
        }

        return listOfAddresses;
    }

}
