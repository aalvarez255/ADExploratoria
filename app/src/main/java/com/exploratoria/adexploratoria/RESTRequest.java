package com.exploratoria.adexploratoria;

import android.app.Activity;
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
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

/**
 * Created by adrian on 20/11/14.
 */
public class RESTRequest extends AsyncTask<Void,Integer,Void> {

    Activity context;
    String tipo;
    Menu optionsMenu;

    Bitmap bitmap = null;
    String titulo = "";
    String año = "";
    String idm = "";

    boolean errorNet = false;
    boolean errorServ = false;

    public RESTRequest (Activity context, String tipo, Menu optionsMenu) {
        this.context = context;
        this.tipo = tipo;
        this.optionsMenu = optionsMenu;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JSONObject res = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://api.series.ly/v2/auth_token?id_api=3074&secret=x6u6xTFr6h3CyVebSVcu");
            HttpResponse response = httpClient.execute(request);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for(String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            JSONObject json = new JSONObject(tokener);

            String auth_token  = json.getString("auth_token");

            Random random = new Random();
            int page = random.nextInt((2 - 0) + 1) + 0;  //[0,2]
            if (tipo == "movies") {
                request = new HttpGet("http://api.series.ly/v2/media/browse?auth_token=" + auth_token + "&mediaType=2&limit=48&page=" + Integer.toString(page));
            }
            else {
                request = new HttpGet("http://api.series.ly/v2/media/browse?auth_token="+auth_token+"&mediaType=1&limit=48&page="+Integer.toString(page));
            }
            response = httpClient.execute(request);

            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            builder = new StringBuilder();
            for(String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            tokener = new JSONTokener(builder.toString());
            json = new JSONObject(tokener);
            json = json.getJSONObject("results");
            JSONArray movies = json.getJSONArray("medias");
            int peli = random.nextInt((47 - 0) + 1) + 0;  //[0,47]
            res = movies.getJSONObject(peli);

            idm = res.getString("idm");
            titulo = res.getString("name");
            año = res.getString("year");
            JSONObject portada = res.getJSONObject("poster");
            String url = portada.getString("large");


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
            errorServ = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void jsonObject) {
        super.onPostExecute(jsonObject);
        setRefreshActionButtonState(false);
        if (!errorNet && !errorServ) {
            TextView MovieAño = (TextView) context.findViewById(R.id.MovieAño);
            ImageView MoviePortada = (ImageView) context.findViewById(R.id.MoviePortada);
            TextView MovieTitulo = (TextView) context.findViewById(R.id.MovieTitulo);
            MoviePortada.setImageBitmap(bitmap);
            MovieAño.setText(año);
            MovieTitulo.setText(titulo);

            SharedPreferences preferences =  context.getSharedPreferences("Context", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("idm",idm);
            editor.commit();
        }
        else if (!errorNet && errorServ){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Error")
                    .setMessage("Error interno del servidor. Vuelva a intentarlo más adelante.")
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
    protected void onPreExecute() {
        super.onPreExecute();
        setRefreshActionButtonState(true);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            errorNet = true;

            setRefreshActionButtonState(false);
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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {
        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu
                    .findItem(R.id.refresh);
            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.progress_refresh);
                } else {
                    refreshItem.setActionView(null);
                }
            }
        }
    }
}
