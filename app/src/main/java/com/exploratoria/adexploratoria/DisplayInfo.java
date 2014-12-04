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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.concurrent.ExecutionException;

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
        try {
            boolean b = new getFullPlot().execute().get();
        } catch(Exception e) {
            e.printStackTrace();
        }


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

    private class getFullPlot extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpGet request = new HttpGet("http://api.series.ly/v2/auth_token?id_api=3074&secret=x6u6xTFr6h3CyVebSVcu");
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 3000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 5000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpResponse response = httpClient.execute(request);

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                JSONTokener tokener = new JSONTokener(builder.toString());
                JSONObject json = new JSONObject(tokener);

                String auth_token = json.getString("auth_token");

                String idm = preferences.getString("idm","");
                String tipo = preferences.getString("tipo","");
                if (tipo.equals("Película")) {
                    request = new HttpGet("http://api.series.ly/v2/media/full_info?auth_token=" + auth_token + "&idm=" + idm + "&mediaType=2");
                } else {
                    request = new HttpGet("http://api.series.ly/v2/media/full_info?auth_token=" + auth_token + "&idm=" + idm + "&mediaType=1");
                }
                response = httpClient.execute(request);

                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                builder = new StringBuilder();
                for (String line; (line = reader.readLine()) != null; ) {
                    builder.append(line).append("\n");
                }
                tokener = new JSONTokener(builder.toString());
                JSONObject res = new JSONObject(tokener);

                String plot = res.optString("plot_es");
                if (plot.equals("")) plot = res.optString("plot");
                String titulo = res.optString("name");
                String año = res.optString("year");
                String rating = res.optString("rating");
                String genero = res.optString("maingenre");
                JSONObject portada = res.getJSONObject("poster");
                String urlBig = portada.getString("large");

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("plot",plot);
                editor.putString("idm", idm);
                editor.putString("titulo", titulo);
                editor.putString("tipo", tipo);
                editor.putString("año", año);
                editor.putString("portadaBig", urlBig);
                editor.putString("rating", rating);
                editor.putString("genero", genero);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    }
}
