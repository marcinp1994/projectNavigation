package com.example.marcin.osmtest;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.marcin.osmtest.NavBikeActivity.keyForMapQuest;

public class SearchActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    ListView listView;
    Context context;
    HashMap<String, Address> addressesMap;

    ArrayList<Address> addresses = null;
    AddressFromName addressFromName = new AddressFromName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editText = (EditText) findViewById(R.id.destination);
        button = (Button) findViewById(R.id.buttonSearchDep);
        context = this;
        button.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        InputMethodManager inputManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);

                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });

        listView = (ListView) findViewById(android.R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                Address address = addressesMap.get(adapter.getItem(i));

                AddressesDataSource datasource = new AddressesDataSource(context);
                datasource.open();
                datasource.createDatabaseAddress(adapter.getItem(i), address.getLatitude(), address.getLongitude());

                Intent intent = new Intent();
                intent.putExtra("lon", address.getLongitude());
                intent.putExtra("lat", address.getLatitude());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                addresses = addressFromName.getFromLocationName(editText.getText().toString(), 10, keyForMapQuest);
                ArrayAdapter<String> addressArrayAdapter = (ArrayAdapter<String>) listView.getAdapter();
                addressArrayAdapter.clear();
                addressesMap = new HashMap<>();

                if (addresses.size() == 0) {
                    Toast.makeText(context, "Bad address: " + editText.getText().toString(), Toast.LENGTH_SHORT).show();
                }

                for (Address address : addresses)
                {
                    StringBuilder sb = new StringBuilder();
                    String adressInfo;
                    String addressName;
                    String displayName = (String) address.getExtras().get("display_name");
                    if(displayName == null) {
                        if (address.getMaxAddressLineIndex() == 0) {
                            if (address.getSubAdminArea() == null) {
                                break;
                            } else {
                                adressInfo = address.getCountryName() + ", " + address.getSubAdminArea();
                                sb.append(adressInfo);
                            }

                        } else {
                            for (int a = 0; a < address.getMaxAddressLineIndex() + 1; a++) {
                                adressInfo = address.getAddressLine(a);
                                if (a == address.getMaxAddressLineIndex()) {

                                } else {
                                    adressInfo += ", ";
                                }
                                sb.append(adressInfo);
                            }
                        }
                        addressName = sb.toString();
                    }
                    else
                    {
                        addressName = displayName;
                    }

                    addressArrayAdapter.add(addressName);
                    addressesMap.put(addressName, address);
                }
            }
        });
    }


}
