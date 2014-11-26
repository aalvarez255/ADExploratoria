package com.exploratoria.adexploratoria;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by adrian on 19/11/14.
 */
public class NavigationAdapter extends BaseAdapter {

    private Activity activity;
    ArrayList<ItemDrawer> arrayItems;

    public NavigationAdapter(Activity activity, ArrayList<ItemDrawer> arrayItems) {
        super();
        this.activity = activity;
        this.arrayItems = arrayItems;
    }

    @Override
    public Object getItem(int position) {
        return arrayItems.get(position);
    }

    @Override
    public int getCount() {
        return arrayItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*EXTENDER CUANDO TENGAMOS TODOS LOS DATOS DE LA API*/
    public static class Fila {
        TextView titulo;
        ImageView portada;
        TextView año;
        TextView tipo;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Fila view;
        LayoutInflater inflater = activity.getLayoutInflater();
        if(convertView == null) {
            view = new Fila();
            ItemDrawer item = arrayItems.get(position);
            convertView = inflater.inflate(R.layout.drawer_item, null);
            view.titulo = (TextView) convertView.findViewById(R.id.title_item);
            view.titulo.setText(item.getTitulo());
            view.portada = (ImageView) convertView.findViewById(R.id.portada);
            view.portada.setImageBitmap(item.getPortada());
            view.año = (TextView) convertView.findViewById(R.id.year_item);
            view.año.setText(String.valueOf(item.getYear()));
            view.tipo = (TextView) convertView.findViewById(R.id.type_item);
            view.tipo.setText(item.getTipo());
            convertView.setTag(view);
        }
        else {
            view = (Fila) convertView.getTag();
        }
        return convertView;
    }
}
