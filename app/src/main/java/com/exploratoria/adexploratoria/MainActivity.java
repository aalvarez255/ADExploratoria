package com.exploratoria.adexploratoria;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Para tratar la toolbar como action bar:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getApplicationContext();
        preferences = getApplicationContext().getSharedPreferences("Context", MODE_PRIVATE);
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

    public void gotoSeen(View v) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("goto","seen");
        edit.commit();
        Intent intent = new Intent(this, SeenList.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //aaaasdff

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
