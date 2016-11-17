package com.example.marcin.osmtest;

import android.content.Context;
import android.os.AsyncTask;

import org.osmdroid.views.MapView;

/**
 * Created by Marcin on 17.11.2016.
 */

public class RoadAsyncTask extends AsyncTask<Object,Void,MapView>
{
    private final Context mContext;

    public RoadAsyncTask(Context context) {
        this.mContext = context;
    }


    @Override
    protected MapView doInBackground(Object... objects) {

        return null;
    }
}
