package com.orgdobryva.moviedb;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dmytro on 18/04/16.
 */
public class DatabaseFilms extends SQLiteOpenHelper {

    static final String DB_NAME = "mydb";
    static final int DB_VERSION = 1;

    public DatabaseFilms(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE favorites (_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " film_id INTEGER," +
                " film_name TEXT," +
//                " film_image BLOB, " +
                " poster_path TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
