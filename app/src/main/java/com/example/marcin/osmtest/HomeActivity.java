package com.example.marcin.osmtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void showMapOnClick(View view)
    {
        Intent goToIntent = new Intent(this, MainActivity.class);
        startActivity(goToIntent);
    }

    public void navByCarOnClick(View view)
    {
        Intent goToIntent = new Intent(this, NavCarActivity.class);
        startActivity(goToIntent);
    }
}

