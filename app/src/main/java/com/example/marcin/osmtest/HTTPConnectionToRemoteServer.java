package com.example.marcin.osmtest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Marcin on 11.11.2016.
 */

 class HTTPConnectionToRemoteServer
{
    private static String responseFromOSRM;
    private static String userAgent;

    private static String notConnected;

    void sendRequest(String Url)
    {
            URL url;
            //stworzenie obiektu do polaczenia typu http
            HttpURLConnection urlConnection;
            try
            {
                url = new URL(Url);
                //otwarcie polaczenia http
                urlConnection = (HttpURLConnection) url.openConnection();
                //nadanie polaczeniu hhtp odpowiednich ustawien
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("User-Agent", userAgent);
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(false);
                urlConnection.setConnectTimeout(100000);
                urlConnection.setReadTimeout(100000);
                try
                {
                    urlConnection.connect();
                    int responseStatus = urlConnection.getResponseCode();
                    if(responseStatus == HttpURLConnection.HTTP_OK)
                    {
                        responseFromOSRM = convertStream(urlConnection.getInputStream());
                    }
                }
                catch (Exception e)
                {
                    notConnected = "NotConnected";
                    responseFromOSRM = null;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            }

    /*
     * Konwersja responsu z InputStreamu na String
     */
    private String convertStream(InputStream inputStream)
    {
        BufferedReader bufferedReader = null;
        StringBuilder responseString = new StringBuilder();
        try
        {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
            {
                try {
                    bufferedReader.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return responseString.toString();
    }


     static String getResponseFromOSRM()
     {
        return responseFromOSRM;
    }
    public static String getNotConnected() {
        return notConnected;
    }
      void setUserAgent(String userAgent)
      {
        HTTPConnectionToRemoteServer.userAgent = userAgent;
    }
}
