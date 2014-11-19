package com.exploratoria.adexploratoria;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SeenList extends ActionBarActivity {

    protected DrawerLayout NavDrawerLayout;
    private ListView NavList;

    private String[] titulos;
    private ArrayList<ItemDrawer> NavItems;
    NavigationAdapter NavAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavList = (ListView) findViewById(R.id.left_drawer);

        View header = getLayoutInflater().inflate(R.layout.drawer_header,null);
        NavList.addHeaderView(header);
        titulos = getResources().getStringArray(R.array.test_drawer);

        NavItems = new ArrayList<ItemDrawer>();
        int id_foto = R.drawable.ic_launcher;

        NavItems.add(new ItemDrawer(titulos[0],2014,"Serie",id_foto));
        NavItems.add(new ItemDrawer(titulos[1],2014,"Pelicula",id_foto));
        NavItems.add(new ItemDrawer(titulos[2],2014,"Serie",id_foto));
        NavItems.add(new ItemDrawer(titulos[3],2001,"Pelicula",id_foto));
        NavItems.add(new ItemDrawer(titulos[4],2019,"Serie",id_foto));
        NavItems.add(new ItemDrawer(titulos[5],2014,"Serie",id_foto));

        NavAdapter = new NavigationAdapter(this,NavItems);
        NavList.setAdapter(NavAdapter);
    }
}