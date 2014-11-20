package com.exploratoria.adexploratoria;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;

public class DisplayAdvice extends SeenList {

    SharedPreferences preferences;
    TextView titulo;

    CardView cardView;

    TextView MovieTitulo;
    ImageView MoviePortada;
    TextView MovieAño;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.display_advise,null,false);
        NavDrawerLayout.addView(contentView,0);

        cardView = (CardView) findViewById(R.id.card_view);
        MovieAño = (TextView) findViewById(R.id.MovieAño);
        MoviePortada = (ImageView) findViewById(R.id.MoviePortada);
        MovieTitulo = (TextView) findViewById(R.id.MovieTitulo);
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
        String context = preferences.getString("goto","");

        if(context.equals("movies")) {
            titulo.setText("MOVIES");
            try {
                RESTRequest request = new RESTRequest(this,"movies");
                JSONObject json = request.execute().get();

                String idm = json.getString("idm");
                String titulo = json.getString("name");
                String año = json.getString("year");
                JSONObject portada = json.getJSONObject("poster");
                String url = portada.getString("large");

                Bitmap image = new ImageRequest().execute(url).get();

                MoviePortada.setImageBitmap(image);
                MovieAño.setText(año);
                MovieTitulo.setText(titulo);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("idm",idm);
                editor.commit();

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
        else if(context.equals("series")) {
            titulo.setText("SERIES");
        }


    }
}
