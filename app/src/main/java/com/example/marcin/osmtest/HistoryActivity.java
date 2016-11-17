package com.example.marcin.osmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static AddressesDataSource datasource;
    static ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         listView = (ListView) findViewById(android.R.id.list);
        toolbar.setTitle("Historia adresów");
        setSupportActionBar(toolbar);

        datasource = new AddressesDataSource(this);
        datasource.open();

        List<DatabaseAddress> values = datasource.getAllAddressesFromDatabase();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<DatabaseAddress> adapter = new ArrayAdapter<DatabaseAddress>(this,
                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<DatabaseAddress> adapter = (ArrayAdapter<DatabaseAddress>) listView.getAdapter();
                DatabaseAddress comment = adapter.getItem(i);
                double latitude = comment.getLatitude();
                double longitude = comment.getLatitude();
                Intent myIntent = new Intent(view.getContext(), NavBikeActivity.class);
                startActivityForResult(myIntent, 0);

//                datasource.deleteAddressInDatabase(comment);
//                adapter.remove(comment);
            }
        });
    }


//    // Will be called via the onClick attribute
//    // of the buttons in main.xml
//    public void OnClick(View view) {
//        @SuppressWarnings("unchecked")
//        ArrayAdapter<DatabaseAddress> adapter = (ArrayAdapter<DatabaseAddress>) listView.getAdapter();
//        DatabaseAddress databaseAddress = null;
//        switch (view.getId()) {
//            case R.id.add:
//                Address address = getAdres();
//                StringBuilder sb = new StringBuilder();
//                for(int a= 0 ; a< address.getMaxAddressLineIndex(); a++ )
//                {
//                    String addressInfo = address.getAddressLine(a) + ", ";
//                    sb.append(addressInfo);
//                }
//                String addressName = sb.toString();
//                double latitude = getLatitude();
//                double longitude = getLongitude();
//                databaseAddress = datasource.createDatabaseAddress(addressName, latitude, longitude);
//                adapter.add(databaseAddress);
//                break;
//            case R.id.delete:
//                if (listView.getAdapter().getCount() > 0) {
//                    databaseAddress = (DatabaseAddress) listView.getAdapter().getItem(0);
//                    datasource.deleteAddressInDatabase(databaseAddress);
//                    adapter.remove(databaseAddress);
//                }
//                break;
//        }
//        adapter.notifyDataSetChanged();
//    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        ArrayAdapter<DatabaseAddress> adapter = (ArrayAdapter<DatabaseAddress>) listView.getAdapter();
        DatabaseAddress comment = null;
        int id = item.getItemId();
        switch (id)
        {
            case R.id.delete_history:
            {
                int numberOfAdresses = adapter.getCount();
                if (numberOfAdresses > 0) {

                    for(int i=adapter.getCount()-1; i>=0; i--)
                    {
                        comment = (DatabaseAddress) adapter.getItem(i);
                        datasource.deleteAddressInDatabase(comment);
                        adapter.remove(comment);
                    }
                }
            }
            case R.id.delete_his:
            {
                int numberOfAdresses = adapter.getCount();
                if (numberOfAdresses > 0) {

                    for(int i=adapter.getCount()-1; i>=0; i--)
                    {
                        comment = (DatabaseAddress) adapter.getItem(i);
                        datasource.deleteAddressInDatabase(comment);
                        adapter.remove(comment);
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}