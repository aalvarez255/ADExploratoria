package com.exploratoria.adexploratoria;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by adrian on 21/11/14.
 */
public class ImageRequest extends AsyncTask<String,Void,Bitmap> {
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap res = null;
        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            res = BitmapFactory.decodeStream(inputStream,null,options);

        }catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
