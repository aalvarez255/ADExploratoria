package com.exploratoria.adexploratoria;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class DisplayAdvice extends ActionBarActivity {

    SharedPreferences preferences;
    TextView titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_advise);

        titulo = (TextView) findViewById(R.id.TextTitulo);
        preferences = getApplicationContext().getSharedPreferences("Context", getApplicationContext().MODE_PRIVATE);
        String context = preferences.getString("goto","");

        if(context.equals("movies")) titulo.setText("MOVIES");
        else if(context.equals("series")) titulo.setText("SERIES");
    }
}
