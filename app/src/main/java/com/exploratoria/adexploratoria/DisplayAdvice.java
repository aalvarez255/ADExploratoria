package com.exploratoria.adexploratoria;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DisplayAdvice extends SeenList {

    SharedPreferences preferences;
    TextView titulo;

    CardView cardView;

    String context;

    Menu optionsMenu;

    IntentsOpenHelpers sql;

    Activity activity;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.display_advise,null,false);
        NavDrawerLayout.addView(contentView,0);

        cardView = (CardView) findViewById(R.id.card_view);

        //cardView.setVisibility(View.INVISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_launcher);

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // A String resource to describe the "open drawer" action for accessibility
        // A String resource to describe the "close drawer" action for accessibility
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

        titulo = (TextView) findViewById(R.id.TextTitulo);
        getApplicationContext();
        preferences = getApplicationContext().getSharedPreferences("Context", MODE_PRIVATE);
        context = preferences.getString("goto","");

        if(context.equals("movies")) {
            titulo.setText("PELÍCULAS");
            try {
                RESTRequest request = new RESTRequest(this,"movies",optionsMenu);
                request.execute();

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        else if(context.equals("series")) {
            titulo.setText("SERIES");
            try {
                RESTRequest request = new RESTRequest(this,"series",optionsMenu);
                request.execute();

            }catch(Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.optionsMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                // CODE REFRESCAR
                if(context.equals("movies")) {
                    titulo.setText("PELÍCULAS");
                    try {
                        RESTRequest request = new RESTRequest(this,"movies",optionsMenu);
                        request.execute();
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(context.equals("series")) {
                    titulo.setText("SERIES");
                    try {

                        RESTRequest request = new RESTRequest(this,"series",optionsMenu);
                        request.execute();
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            case R.id.markseen:
                try {
                    Boolean bool = new StoreMovieDB().execute().get();
                    if (bool) updateDrawer();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void gotoInfo(View v) {
        Intent intent = new Intent(this, DisplayInfo.class);
        startActivity(intent);
    }


    private class StoreMovieDB extends AsyncTask<Void,Void,Boolean> {

        String tipo;

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid) {
                Toast.makeText(getApplicationContext(),tipo+" añadida a la lista de vistas.",
                        Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Error: "+tipo+" no añadida a la lista de vistas.",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String titulo = preferences.getString("titulo","");
                tipo = preferences.getString("tipo","");
                String portadaString = preferences.getString("portada","");
                int año = Integer.parseInt(preferences.getString("año", ""));
                String idm = preferences.getString("idm","");

                URL imageUrl = new URL(portadaString);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bArray = bos.toByteArray();

                sql = new IntentsOpenHelpers(getApplicationContext());
                SQLiteDatabase db = sql.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put("idm", idm);
                cv.put("titulo", titulo);
                cv.put("tipo", tipo);
                cv.put("año", año);
                cv.put("portada", bArray);
                db.insertWithOnConflict("vistas", null, cv, SQLiteDatabase.CONFLICT_REPLACE);

                db.close();

            }catch(Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}

