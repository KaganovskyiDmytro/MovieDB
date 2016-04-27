package com.orgdobryva.moviedb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class FilmDetailsFragment extends Fragment {

    private final String MOVIE_BASE_URL_DETAILED = "http://api.themoviedb.org/3/movie/";
    private final String APPID_PARAM = "api_key";

    private ArrayList<String> genresType = new ArrayList<>();


    public FilmDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_film_details, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {
            int id = arguments.getInt("id");

            Uri buildUri = Uri.parse(MOVIE_BASE_URL_DETAILED).buildUpon()
                    .appendPath(Integer.toString(id))
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                    .build();

            DetailsDownloader detailsDownloader = new DetailsDownloader();
            detailsDownloader.execute(buildUri.toString());
        }


        return view;
    }

    public class DetailsDownloader extends AsyncTask<String, Bundle, Bundle> {

        @Override
        protected Bundle doInBackground(String... params) {

            try {
                JSONObject object = null;

                try {
                    String json_str = IOUtils.toString(new URL(params[0]).openStream());
                    object = new JSONObject(json_str);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bundle bundle = new Bundle();
                bundle.putString("name", object.getString("title"));
                bundle.putString("poster", object.getString("poster_path"));
                bundle.putString("overview", object.getString("overview"));
                bundle.putDouble("rating", object.getDouble("vote_average"));
                bundle.putString("year", object.getString("release_date"));

                JSONArray genreArray = object.getJSONArray("genres");

                StringBuilder sb = new StringBuilder(genreArray.getJSONObject(0).getString("name"));

                for (int i = 1; i < genreArray.length(); i++) {
                    sb.append(", ").append(genreArray.getJSONObject(i).getString("name"));
                }

                bundle.putString("genres", sb.toString());

                return bundle;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bundle filmBundle) {

            if (filmBundle != null) {
                TextView tvFilmName = (TextView) getView().findViewById(R.id.detailedFilmName);
                tvFilmName.setText(filmBundle.getString("name"));

                TextView tvFilmReit = (TextView) getView().findViewById(R.id.detailedFilmRate);
                tvFilmReit.setText(Double.toString(filmBundle.getDouble("rating")));

                TextView tvFilmYear = (TextView) getView().findViewById(R.id.detailedFilmYear);
                tvFilmYear.setText(filmBundle.getString("year"));

                TextView tvFilmOverview = (TextView) getView().findViewById(R.id.filmOverview);
                tvFilmOverview.setText(filmBundle.getString("overview"));

                TextView tvGenres = (TextView) getView().findViewById(R.id.detailedFilmType);
                tvGenres.setText(filmBundle.getString("genres"));

                ImageView ivPoster = (ImageView) getView().findViewById(R.id.detailedFilmPoster);

                String posterPath = filmBundle.getString("poster");

                new PosterDownloadTask(ivPoster).execute(posterPath);
            }
        }
    }

    private class PosterDownloadTask extends AsyncTask<String, Void, Bitmap> {

        private static final String POSTER_PATH = "http://image.tmdb.org/t/p/w320";
        private ImageView mImageView;

        public PosterDownloadTask(ImageView mImageView) {
            this.mImageView = mImageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            if (params.length > 0) {
                String path = params[0];
                String address = POSTER_PATH.concat(path);

                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(address).openStream());

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
            mImageView.setImageBitmap(bitmap);
        }
    }
}
