package com.exploratoria.adexploratoria;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.ProtectionDomain;

/**
 * Created by adrian on 26/11/14.
 */

/*
    Portada, titulo, año, tipo, plot, rating, genero,
 */
public class DisplayInfo extends SeenList {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.display_info,null,false);
        NavDrawerLayout.addView(contentView,0);

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

        preferences = getApplicationContext().getSharedPreferences("Context", MODE_PRIVATE);

        TextView titulo = (TextView) findViewById(R.id.tituloInfo);
        TextView año = (TextView) findViewById(R.id.añoInfo);
        TextView tipo = (TextView) findViewById(R.id.tipoInfo);
        TextView rating = (TextView) findViewById(R.id.ratingInfo);
        TextView plot = (TextView) findViewById(R.id.plotInfo);
        TextView genero = (TextView) findViewById(R.id.generoInfo);
        ImageView portada  = (ImageView) findViewById(R.id.portadaInfo);

        Bitmap image = null;
        try {
            String url = preferences.getString("portadaBig","");
            image = new getPortadaUrl().execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        titulo.setText(preferences.getString("titulo",""));
        tipo.setText(preferences.getString("tipo",""));
        año.setText(preferences.getString("año",""));
        rating.setText(preferences.getString("rating",""));
        if (image != null) portada.setImageBitmap(image);
        plot.setText(preferences.getString("plot",""));
        genero.setText(preferences.getString("genero",""));
    }

    private class getPortadaUrl extends AsyncTask<String,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                String url = params[0];
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                bitmap = BitmapFactory.decodeStream(inputStream,null,options);

            }catch(Exception e) {
                e.printStackTrace();
                return null;
            }
            return bitmap;
        }

    }
}
