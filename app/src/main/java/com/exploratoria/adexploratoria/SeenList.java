package com.exploratoria.adexploratoria;

import android.content.SharedPreferences;
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

public class SeenList extends ActionBarActivity implements AdapterView.OnItemClickListener {

    SharedPreferences preferences;

    private DrawerLayout mDrawer;
    private ListView mDrawerOptions;
    private static final String[] values = {"Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer", "Drawer"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);

        // Para tratar la toolbar como action bar:
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mDrawerOptions = (ListView) findViewById(R.id.left_drawer);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerOptions.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values));
        mDrawerOptions.setOnItemClickListener(this);

        getApplicationContext();
        preferences = getApplicationContext().getSharedPreferences("Context", MODE_PRIVATE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Toast.makeText(this, "Pulsado " + values[i], Toast.LENGTH_SHORT).show();
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (mDrawer.isDrawerOpen(mDrawerOptions)){
                    mDrawer.closeDrawers();
                }else{
                    mDrawer.openDrawer(mDrawerOptions);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
