package com.exploratoria.adexploratoria;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Base64;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    String rating = "";
    String año = "";
    String idm = "";
    String urlSmall = "";

    boolean errorNet = false;
    boolean errorServ = false;

    ProgressDialog progressDialog;

    IntentsOpenHelpers sql;

    public RESTRequest (Activity context, String tipo, Menu optionsMenu) {
        this.context = context;
        this.tipo = tipo;
        this.optionsMenu = optionsMenu;
    }

    @Override
    protected Void doInBackground(Void... params) {
        JSONObject res = null;
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
            for(String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            JSONObject json = new JSONObject(tokener);

            String auth_token  = json.getString("auth_token");

            Random random = new Random();
            int num_pag = 2;
            int num_pag_min = 0;
            int page = random.nextInt((num_pag - num_pag_min) + 1) + num_pag_min;  //[0,2]
            if (tipo == "movies") {
                tipo = "Película";
                request = new HttpGet("http://api.series.ly/v2/media/browse?auth_token=" + auth_token + "&mediaType=2&limit=48&page=" + Integer.toString(page));
            }
            else {
                tipo = "Serie";
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

            ArrayList<String> idm_vistas = new ArrayList<String>();
            sql = new IntentsOpenHelpers(context);
            SQLiteDatabase db = sql.getWritableDatabase();
            String consult = "SELECT idm FROM vistas";
            Cursor cursor = db.rawQuery(consult,null);
            while (cursor.moveToNext()) {
                idm_vistas.add(cursor.getString(0));
            }
            cursor.close();
            db.close();

            if (idm_vistas.size() == (num_pag*48)) {
                num_pag_min += (num_pag - num_pag_min) + 1;
                num_pag += (num_pag - num_pag_min) + 1;
            }

            idm = res.getString("idm");

            while (idm_vistas.contains(idm)) {
                random = new Random();
                page = random.nextInt((num_pag - num_pag_min) + 1) + num_pag_min;  //[0,2]
                if (tipo == "movies") {
                    tipo = "Película";
                    request = new HttpGet("http://api.series.ly/v2/media/browse?auth_token=" + auth_token + "&mediaType=2&limit=48&page=" + Integer.toString(page));
                }
                else {
                    tipo = "Serie";
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
                movies = json.getJSONArray("medias");
                peli = random.nextInt((47 - 0) + 1) + 0;  //[0,47]
                res = movies.getJSONObject(peli);
                idm = res.getString("idm");
            }
            titulo = res.getString("name");
            año = res.getString("year");
            rating = res.getString("rating");
            double rat = Double.parseDouble(rating);
            rat = Math.round( rat * 100.0 ) / 100.0;
            rating = String.valueOf(rat);
            JSONObject portada = res.getJSONObject("poster");
            String url = portada.getString("large");
            urlSmall = portada.getString("small");

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
        progressDialog.dismiss();
        if (!errorNet && !errorServ) {
            TextView MovieAño = (TextView) context.findViewById(R.id.MovieAño);
            ImageView MoviePortada = (ImageView) context.findViewById(R.id.MoviePortada);
            TextView MovieTitulo = (TextView) context.findViewById(R.id.MovieTitulo);
            TextView MovieRating = (TextView) context.findViewById(R.id.MovieRating);
            MoviePortada.setImageBitmap(bitmap);
            MovieAño.setText(año);
            MovieTitulo.setText(titulo);
            MovieRating.setText(rating);

            SharedPreferences preferences =  context.getSharedPreferences("Context", context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("idm",idm);
            editor.putString("titulo",titulo);
            editor.putString("tipo",tipo);
            editor.putString("año",año);
            editor.putString("portada",urlSmall);

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

        progressDialog = ProgressDialog.show(context,"Cargando","Descargando información..",true,false);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            errorNet = true;
            progressDialog.dismiss();
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
