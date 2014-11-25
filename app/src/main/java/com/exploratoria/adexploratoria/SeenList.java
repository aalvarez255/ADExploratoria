package com.exploratoria.adexploratoria;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Blob;
import java.util.ArrayList;

public class SeenList extends ActionBarActivity {

    protected DrawerLayout NavDrawerLayout;
    private ListView NavList;

    private ArrayList<ItemDrawer> NavItems;
    public NavigationAdapter NavAdapter;

    IntentsOpenHelpers sql;

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

        NavItems = new ArrayList<ItemDrawer>();

        sql = new IntentsOpenHelpers(getApplicationContext());
        SQLiteDatabase db = sql.getWritableDatabase();
        String consult = "SELECT * FROM vistas";

        Cursor cursor = db.rawQuery(consult,null);
        while (cursor.moveToNext()) {
            String titulo = cursor.getString(2);
            String tipo = cursor.getString(3);
            int año = cursor.getInt(4);
            byte[] portadaByte = cursor.getBlob(5);

            Bitmap bitmap = BitmapFactory.decodeByteArray(portadaByte , 0, portadaByte.length);

            NavItems.add(new ItemDrawer(titulo,año,tipo,bitmap));
        }
        cursor.close();
        sql.close();

        NavAdapter = new NavigationAdapter(this,NavItems);
        NavList.setAdapter(NavAdapter);
    }

    public void updateDrawer() {
        Log.v("VIST", "entra");
        NavItems = new ArrayList<ItemDrawer>();

        sql = new IntentsOpenHelpers(getApplicationContext());
        SQLiteDatabase db = sql.getWritableDatabase();
        String consult = "SELECT * FROM vistas";

        Cursor cursor = db.rawQuery(consult,null);
        while (cursor.moveToNext()) {
            String titulo = cursor.getString(2);
            String tipo = cursor.getString(3);
            int año = cursor.getInt(4);
            byte[] portadaByte = cursor.getBlob(5);

            Bitmap bitmap = BitmapFactory.decodeByteArray(portadaByte , 0, portadaByte.length);

            NavItems.add(new ItemDrawer(titulo,año,tipo,bitmap));
        }
        cursor.close();
        sql.close();
        NavAdapter = new NavigationAdapter(this,NavItems);
        NavList.setAdapter(NavAdapter);
    }
}