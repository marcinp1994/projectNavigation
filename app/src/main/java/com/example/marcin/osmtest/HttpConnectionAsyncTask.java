package com.example.marcin.osmtest;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.marcin.osmtest.HTTPConnectionToRemoteServer.convertStream;
import static com.example.marcin.osmtest.HTTPConnectionToRemoteServer.responseFromOSRM;
import static com.example.marcin.osmtest.HTTPConnectionToRemoteServer.statusResponse;
import static com.example.marcin.osmtest.HTTPConnectionToRemoteServer.userAgent;

/**
 * Created by Marcin on 18.11.2016.
 */
class HttpConnectionAsyncTask extends AsyncTask<String,Void,String>
{
    String httpUrl;
    public HttpConnectionAsyncTask(String httpUrl)
    {
        this.httpUrl = httpUrl;

    }
    protected String  doInBackground(String... params) {
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("User-Agent", userAgent);
            urlConnection.setUseCaches(false);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setConnectTimeout(100000);
            urlConnection.setReadTimeout(100000);
            urlConnection.connect();
            statusResponse = urlConnection.getResponseCode();
            if(statusResponse == HttpURLConnection.HTTP_OK)
            {
                responseFromOSRM = convertStream(urlConnection.getInputStream());
            }
            urlConnection.disconnect();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return responseFromOSRM;
    }

    protected void onPostExecute(String result)
    {
        responseFromOSRM = result;
    }
}
