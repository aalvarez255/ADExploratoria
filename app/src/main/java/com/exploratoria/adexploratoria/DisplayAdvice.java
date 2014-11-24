package com.exploratoria.adexploratoria;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DisplayAdvice extends SeenList {

    SharedPreferences preferences;
    TextView titulo;

    CardView cardView;

    String context;

    Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            titulo.setText("MOVIES");
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
                    titulo.setText("MOVIES");
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
                       // setRefreshActionButtonState(false);
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            case R.id.markseen:
                // CODE MARCAR COMO VISTA
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
