package com.exploratoria.adexploratoria;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends SeenList {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main,null,false);
        NavDrawerLayout.addView(contentView, 0);
        // Para tratar la toolbar como action bar:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_launcher);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        getApplicationContext();
        preferences = getApplicationContext().getSharedPreferences("Context", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateDrawer();
    }

    public void gotoMovies(View v) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("goto","movies");
        edit.commit();
        Intent intent = new Intent(this, DisplayAdvice.class);
        startActivity(intent);
    }

    public void gotoSeries(View v) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("goto","series");
        edit.commit();
        Intent intent = new Intent(this, DisplayAdvice.class);
        startActivity(intent);
    }


}
