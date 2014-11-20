package com.exploratoria.adexploratoria;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by adrian on 20/11/14.
 */
public class RESTRequest extends AsyncTask<Void,Integer,JSONObject> {

    Activity context;
    String tipo;

    public RESTRequest (Activity context, String tipo) {
        this.context = context;
        this.tipo = tipo;
    }

    @Override
    protected JSONObject doInBackground(Void... params) {
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
            request = new HttpGet("http://api.series.ly/v2/media/browse?auth_token="+auth_token+"&mediaType=2&limit=48&page="+Integer.toString(page));
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
        }catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}
