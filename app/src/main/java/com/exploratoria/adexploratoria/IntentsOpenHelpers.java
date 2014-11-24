package com.exploratoria.adexploratoria;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by adrian on 24/11/14.
 */
public class IntentsOpenHelpers extends SQLiteOpenHelper {

    private static int BD_VERSION = 1;
    private static String BD_NAME = "MovieAdviserDB";
    private static String VISTAS_TABLE = "vistas";

    public IntentsOpenHelpers(Context context) {
        super(context, BD_NAME, null, BD_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+VISTAS_TABLE+"( _id INTEGER PRIMARY KEY AUTOINCREMENT, idm TEXT UNIQUE, titulo TEXT, tipo TEXT, año INTEGER, portada BLOB)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+VISTAS_TABLE);
        db.execSQL("CREATE TABLE "+VISTAS_TABLE+"( _id INTEGER PRIMARY KEY AUTOINCREMENT, idm TEXT UNIQUE, titulo TEXT, tipo TEXT, año INTEGER, portada BLOB)");
    }
}

