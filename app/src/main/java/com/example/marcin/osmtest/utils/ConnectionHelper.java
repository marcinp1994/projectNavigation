package com.example.marcin.osmtest.utils;

/**
 * Created by Marcin on 11.11.2016.
 */

public class ConnectionHelper
{
    public static final String LOG = "NAVIGATIONGPS";
    public static final String DEFAULT_USER_AGENT = "navigationgps1";
     static String requestOption;

    /**
     *  Zwraca response jako string lub null jesli wystapił blad.
     */
    public static String getResponseStringFromUrl(String url, String userAgent)
    {
        HTTPConnectionToRemoteServer connection = new HTTPConnectionToRemoteServer();
        if (userAgent != null)
            connection.setUserAgent(userAgent);
        connection.sendRequest(url);
        return HTTPConnectionToRemoteServer.getResponseFromOSRM();
    }

    public void addOptionToRequest(String requestOption){
        ConnectionHelper.requestOption += "&" + requestOption;
    }


    public static String getRequestOption() {
        return requestOption;
    }

    public static void setRequestOption(String requestOption) {
        ConnectionHelper.requestOption = requestOption;
    }

}
