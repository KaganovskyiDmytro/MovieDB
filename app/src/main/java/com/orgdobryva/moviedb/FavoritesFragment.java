package com.orgdobryva.moviedb;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class FavoritesFragment extends Fragment {

    TextView mCustomTitle;
    TableLayout mTableLayout;

    private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w320";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.favorites, menu);
        setCustomTitle("Favorites");
    }

    public void setCustomTitle(CharSequence customTitle) {
       getActivity().setTitle(customTitle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.activity_favorites_table_creator, container, false);
         mCustomTitle = (TextView) view.findViewById(R.id.title);

         setHasOptionsMenu(true);



        mTableLayout = (TableLayout) view.findViewById(R.id.tableLayout_db);

        Uri build = FilmContract.BASE_CONTENT_URI.buildUpon().build();

        Cursor fileCursor = getActivity().getContentResolver().query(build, null, null, null, null);

        while (fileCursor.moveToNext()){

            TableRow  row = new TableRow(getContext());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            String path = POSTER_PATH.concat(fileCursor.getString(3));

            ImageView ivPoster = new ImageView(getContext());
            row.addView(ivPoster);

            Picasso.with(getContext())
                    .load(path)
                    .resize(100, 150)
                    .into(ivPoster);

            TextView tvTitle = new TextView(getContext());
            tvTitle.setText(fileCursor.getString(2));
            row.addView(tvTitle);





            mTableLayout.addView(row);
        }

        return view;
    }


}
