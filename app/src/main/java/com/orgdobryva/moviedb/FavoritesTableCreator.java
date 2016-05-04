package com.orgdobryva.moviedb;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TableLayout;

public class FavoritesTableCreator extends AppCompatActivity {

    private DatabaseFilms dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_table_creator);
    }


}
