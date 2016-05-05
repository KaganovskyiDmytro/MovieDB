package com.orgdobryva.moviedb;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

public class FavoritesFragment extends Fragment {

    private DatabaseFilms dbHelper;
    TableLayout mTableLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_favorites_table_creator, container, false);

        mTableLayout = (TableLayout) view.findViewById(R.id.tableLayout_db);

        Uri build = FilmContract.BASE_CONTENT_URI.buildUpon().build();

        Cursor fileCursor = getActivity().getContentResolver().query(build, null, null, null, null);

        while (fileCursor.moveToNext()){

            TableRow  row = new TableRow(getContext());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            TextView tvTitle = new TextView(getContext());
            tvTitle.setText(fileCursor.getString(2));
            row.addView(tvTitle);

            ImageView ivPoster = new ImageView(getContext());
            row.addView(ivPoster);

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(fileCursor.getBlob(3)));
                ivPoster.setImageBitmap(bitmap);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }

            mTableLayout.addView(row);
        }

        return view;
    }


}
