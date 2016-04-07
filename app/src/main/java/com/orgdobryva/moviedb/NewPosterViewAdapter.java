package com.orgdobryva.moviedb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by human on 3/30/16.
 */
public class NewPosterViewAdapter extends ArrayAdapter<Bundle> {

    private final int MINIMUM_VIEWS = 12;
    private int current = 0, total = 0;

    private Map<String, Bitmap> cache = new LinkedHashMap<>();
    private Map<ImageView, PosterDownloadTask> tasks;

    private Bitmap template;

    public NewPosterViewAdapter(Context context, List<Bundle> bundles) {
        super(context, 0, bundles);
        this.tasks = Collections.synchronizedMap(new HashMap<ImageView, PosterDownloadTask>());

        this.template = BitmapFactory.decodeResource(context.getResources(), R.drawable.pattern);
    }

    private int lastPosition = 0;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bundle bundle = getItem(position);

        Log.i("TOTAL", position + "");

        if (convertView == null || current < MINIMUM_VIEWS) {
            current++;

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

        if (!cache.containsKey(posterPath) || cache.get(posterPath) == null) {
            cache.put(posterPath, null);

            PosterDownloadTask posterDownloadTask = new PosterDownloadTask(ivPoster);

            PosterDownloadTask old = tasks.put(ivPoster, posterDownloadTask);

            if (old != null) {
                old.cancel(true);
            }

            posterDownloadTask.execute(posterPath);
        }

        ivPoster.setImageBitmap(cache.get(posterPath) == null ? template : cache.get(posterPath));

        lastPosition = position;

        return convertView;
    }

    private class PosterDownloadTask extends AsyncTask<String, Void, Bitmap> {

        private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w320";
        private ImageView tvPoster;

        public PosterDownloadTask(ImageView tvPoster) {
            this.tvPoster = tvPoster;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (params.length > 0) {
                String path = params[0];
                String address = POSTER_PATH.concat(path);

                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(address).openStream());
                    cache.put(path, bitmap);

                    return bitmap;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            tvPoster.setImageBitmap(bitmap);
        }
    }
}