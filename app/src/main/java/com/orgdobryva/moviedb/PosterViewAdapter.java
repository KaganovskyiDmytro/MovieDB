package com.orgdobryva.moviedb;

import android.content.Context;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by human on 3/30/16.
 */
public class PosterViewAdapter extends ArrayAdapter<Bundle> {

    private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w320";
//    private final int MINIMUM_VIEWS = 12;
//    private int current = 0, total = 0;

    Context mContext;


    public PosterViewAdapter(Context context, List<Bundle> bundles) {
        super(context, 0, bundles);

        mContext = context;


    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bundle bundle = getItem(position);

        Log.i("TOTAL", position + "");

        if (convertView == null )        {

            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.poster_layout, parent, false);
        }


        TextView tvFilmName = (TextView) convertView.findViewById(R.id.filmName);
        tvFilmName.setText(bundle.getString("name"));

        TextView tvFilmReit = (TextView) convertView.findViewById(R.id.filmReit);
        tvFilmReit.setText(Double.toString(bundle.getDouble("rating")));

        TextView tvFilmYear = (TextView) convertView.findViewById(R.id.filmYear);
        tvFilmYear.setText(bundle.getString("year"));


        ImageView ivPoster = (ImageView) convertView.findViewById(R.id.filmPoster);

        String posterPath = bundle.getString("poster");


        String path = POSTER_PATH.concat(posterPath);

        Picasso.with(getContext())
                .load(path)
                .into(ivPoster);

        return convertView;
    }

}