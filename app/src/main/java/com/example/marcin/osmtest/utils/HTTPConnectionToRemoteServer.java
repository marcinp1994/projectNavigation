package com.example.marcin.osmtest.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

/**
 * Created by Marcin on 11.11.2016.
 */

public class HTTPConnectionToRemoteServer {
     static String responseFromOSRM;
     static String userAgent;

    public static int getStatusResponse() {
        return statusResponse;
    }

    static int statusResponse;

    private static String notConnected;

    String sendRequest(String Url)
    {
        try {
            return new HttpConnectionAsyncTask(Url).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
     * Konwersja responsu z InputStreamu na String
     */
    static String convertStream(InputStream inputStream) {
        BufferedReader bufferedReader = null;
        StringBuilder responseString = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseString.toString();
    }


    static String getResponseFromOSRM() {
        return responseFromOSRM;
    }

    public static String getNotConnected() {
        return notConnected;
    }

    void setUserAgent(String userAgent) {
        HTTPConnectionToRemoteServer.userAgent = userAgent;
    }
}


