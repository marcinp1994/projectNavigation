package com.example.marcin.osmtest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.marcin.osmtest.R;
import com.example.marcin.osmtest.database.AddressesDataSource;

import java.util.ArrayList;
import java.util.List;

public class POIActivity extends AppCompatActivity {
    static ListView listView;
    private static AddressesDataSource datasource;
    Context context;
    View view1;
    String poi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        listView = (ListView) findViewById(android.R.id.list);
        toolbar.setTitle("Wybierz POI");
        setSupportActionBar(toolbar);

        List<String> listOfPOI = new ArrayList<>();
        listOfPOI.add("Lotnisko");
        listOfPOI.add("Bankomat");
        listOfPOI.add("Bank");
        listOfPOI.add("Bar");
        listOfPOI.add("Kantor");
        listOfPOI.add("Dworzec autobusowy");
        listOfPOI.add("Kawiarnia");
        listOfPOI.add("Wypożyczalnia samochodów ");
        listOfPOI.add("Myjnia samochodowa");
        listOfPOI.add("Kasyno");
        listOfPOI.add("Kino");
        listOfPOI.add("Klub");
        listOfPOI.add("Uczelnia");
        listOfPOI.add("Szpital");
        listOfPOI.add("Stacja benzynowa");
        listOfPOI.add("Supermarket");
        listOfPOI.add("Apteka");
        listOfPOI.add("Posterunek policji");
        listOfPOI.add("Poczta");
        listOfPOI.add("Restauracja");
        listOfPOI.add("Postój taksówek");
        listOfPOI.add("Teatr");
        listOfPOI.add("Kościół");
        listOfPOI.add("Park");
        listOfPOI.add("Basen");
        listOfPOI.add("Stadion");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listOfPOI);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                poi = adapter.getItem(i);
                view1 = view;

                Intent intent = new Intent();
                intent.putExtra("poi", poi);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }
}



