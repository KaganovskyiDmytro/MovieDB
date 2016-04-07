package com.orgdobryva.moviedb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TODO: document your custom view class.
 */
public class PosterView extends LinearLayout {

    private TextView tvFilmName;
    private TextView tvFilmReit;
    private TextView tvFilmGenre;
    private TextView tvFilmYear;
    private String path;
    private ImageView ivPoster;

    private Bundle bundle;
    private ImageCacher cacher;

    public PosterView(Context context) {
        super(context);

        inflate(context, R.layout.poster_layout, this);

        this.tvFilmName = (TextView) findViewById(R.id.filmName);
        this.tvFilmReit = (TextView) findViewById(R.id.filmReit);
        this.tvFilmYear = (TextView) findViewById(R.id.filmYear);
        this.tvFilmGenre = (TextView) findViewById(R.id.filmType);
        this.ivPoster = (ImageView) findViewById(R.id.filmPoster);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;

        this.tvFilmName.setText(bundle.getString("name"));
        this.tvFilmGenre.setText(bundle.getString("genre"));
        this.tvFilmReit.setText(Double.toString(bundle.getDouble("rating")));
        this.tvFilmYear.setText(bundle.getString("year"));
        this.path = bundle.getString("poster");

        if (cacher.getCache().containsKey(this.path)) {
            ivPoster.setImageBitmap(cacher.get(this.path));
        } else {
            ivPoster.setImageDrawable(getResources().getDrawable(R.drawable.pattern));
            new ImageDownloader().execute(path);
        }
    }

    public ImageCacher getCacher() {
        return cacher;
    }

    public void setCacher(ImageCacher cacher) {
        this.cacher = cacher;
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        private final String POSTER_PATH = "http://image.tmdb.org/t/p/w320";

        @Override
        protected Bitmap doInBackground(String... params) {
            String path = params[0];
            String address = POSTER_PATH.concat(path);

            try {

                Bitmap bitmap = BitmapFactory.decodeStream(new URL(address).openStream());

                cacher.push(path, bitmap);

                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ivPoster.setImageBitmap(bitmap);
        }
    }
}