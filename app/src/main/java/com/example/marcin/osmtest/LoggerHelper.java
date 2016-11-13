package com.example.marcin.osmtest;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by Marcin on 11.11.2016.
 */

class LoggerHelper
{
     static final String LOG = "NAVIGATIONGPS";
     static final String DEFAULT_USER_AGENT = "navigationgps1";

    /**
     *  Zwraca response jako string lub null jesli wystapił blad.
     */
    static String getResponseStringFromUrl(String url, String userAgent)
    {
        HTTPConnectionToRemoteServer connection = new HTTPConnectionToRemoteServer();
        if (userAgent != null)
            connection.setUserAgent(userAgent);
        connection.sendRequest(url);
        return HTTPConnectionToRemoteServer.getResponseFromOSRM();
    }

    public static HashMap<String, String> parseStringMapResource(Context ctx, int stringArrayResourceId) {
        String[] stringArray = ctx.getResources().getStringArray(stringArrayResourceId);
        HashMap<String, String> map = new HashMap<>(stringArray.length);
        for (String entry : stringArray) {
            String[] splitResult = entry.split("\\|", 2);
            map.put(splitResult[0], splitResult[1]);
        }
        return map;
    }
}
