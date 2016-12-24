package com.example.marcin.osmtest.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marcin.osmtest.R;

import java.util.ArrayList;

import static com.example.marcin.osmtest.location.NavActivity.listOfRoadNodes;
import static com.example.marcin.osmtest.routing.RoadDescription.getLenAndDurAsString;

/**
 * Created by Marcin on 24.11.2016.
 */

public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> stepInstruction;
    private final ArrayList<Integer> directionIcon;

    public CustomListAdapter(Activity context, ArrayList<String> stepInst, ArrayList<Integer> icon) {
        super(context, R.layout.mylist, stepInst);
        this.context=context;
        this.stepInstruction =stepInst;
        this.directionIcon =icon;
    }

    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(stepInstruction.get(position));
        imageView.setImageResource(directionIcon.get(position));
        extratxt.setText(getLenAndDurAsString(context, listOfRoadNodes.get(position).mLength, listOfRoadNodes.get(position).mDuration, true));

//        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
//        TextView extratxt = (TextView) rowView.findViewById(R.id.txt);
//
//        imageView.setImageResource(directionIcon.get(position));
//        extratxt.setText(stepInstruction.get(position));
        return rowView;

    };
}
