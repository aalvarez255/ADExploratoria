package com.exploratoria.adexploratoria;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    TextView titulo;
    TextView año;
    TextView tipo;
    TextView rating;
    TextView plot;
    TextView genero;
    ImageView portada;

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

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

        titulo = (TextView) findViewById(R.id.tituloInfo);
        año = (TextView) findViewById(R.id.añoInfo);
        tipo = (TextView) findViewById(R.id.tipoInfo);
        rating = (TextView) findViewById(R.id.ratingInfo);
        plot = (TextView) findViewById(R.id.plotInfo);
        genero = (TextView) findViewById(R.id.generoInfo);
        portada  = (ImageView) findViewById(R.id.portadaInfo);

        new getFullInfo().execute();
    }


    private class getFullInfo extends AsyncTask<Void,Void,Void> {

        ProgressDialog progressDialog;
        Boolean errorNet = false;
        Boolean errorServ = false;
        Bitmap image;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Cargando", "Descargando información..", true, false);
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                errorNet = true;
                progressDialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context)
                        .setTitle("Error")
                        .setCancelable(false)
                        .setMessage("No hay conexión a Internet.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                cancel(true);
                                context.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                AlertDialog alert = builder1.create();
                alert.show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

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

                URL imageUrl = new URL(urlBig);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                image = BitmapFactory.decodeStream(inputStream,null,options);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("plot",plot);
                editor.putString("idm", idm);
                editor.putString("titulo", titulo);
                editor.putString("tipo", tipo);
                editor.putString("año", año);
                editor.putString("rating", rating);
                editor.putString("genero", genero);
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
                errorServ = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            if (!errorNet && errorServ){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context)
                        .setCancelable(false)
                        .setTitle("Error")
                        .setMessage("No se pudo establecer conexión con el servidor. Vuelva a intentarlo más adelante.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                cancel(true);
                                context.finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert);
                AlertDialog alert = builder1.create();
                alert.show();
            }
            else if (!errorServ && !errorNet) {
                titulo.setText(preferences.getString("titulo", ""));
                tipo.setText(preferences.getString("tipo", ""));
                año.setText(preferences.getString("año", ""));
                rating.setText(preferences.getString("rating", ""));
                portada.setImageBitmap(image);
                plot.setText(preferences.getString("plot", ""));
                genero.setText(preferences.getString("genero", ""));
            }
        }
    }
}
