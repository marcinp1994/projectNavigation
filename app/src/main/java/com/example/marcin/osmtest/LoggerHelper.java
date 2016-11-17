package com.example.marcin.osmtest;

/**
 * Created by Marcin on 11.11.2016.
 */

class LoggerHelper
{
     static final String LOG = "NAVIGATIONGPS";
     static final String DEFAULT_USER_AGENT = "navigationgps1";
     static String requestOption;

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

    public void addOptionToRequest(String requestOption){
        LoggerHelper.requestOption += "&" + requestOption;
    }


    public static String getRequestOption() {
        return requestOption;
    }

    public static void setRequestOption(String requestOption) {
        LoggerHelper.requestOption = requestOption;
    }

}
