package com.example.marcin.osmtest;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcin on 24.11.2016.
 */

class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> stepInstruction;
    private final ArrayList<Integer> directionIcon;

    CustomListAdapter(Activity context, ArrayList<String> stepInst, ArrayList<Integer> icon) {
        super(context, R.layout.list_step, stepInst);
        this.context=context;
        this.stepInstruction =stepInst;
        this.directionIcon =icon;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_step, null,true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        TextView extratxt = (TextView) rowView.findViewById(R.id.txt);

        imageView.setImageResource(directionIcon.get(position));
        extratxt.setText(stepInstruction.get(position));
        return rowView;

    };
}
