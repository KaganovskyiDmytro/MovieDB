package com.orgdobryva.moviedb;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CatalogFragment extends Fragment {

    private final String MOVIEDB_DISCOVER_MOVIE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
    private final String APPID_PARAM = "api_key";
    private final String PAGE_NUMBER_PARAM = "page";

    private List<Bundle> filmBundles;
    private List<Bundle> genreBundeles;
    private PosterViewAdapter posterViewAdapter;
    private ArrayAdapter<Bundle> mGenreArrayAdapter;

    public CatalogFragment() {
        this.filmBundles = new ArrayList<>();
        this.genreBundeles = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.video_catalog, container, false);

        retrievePages();

        this.posterViewAdapter = new PosterViewAdapter(getContext(), filmBundles);

        GridView gridView = (GridView) layout.findViewById(R.id.catalogGridView);
        gridView.setAdapter(posterViewAdapter);


        return layout;
    }

    private void retrievePages(){

        for (int i = 0; i < 10; i++) {
            Uri builtUri = Uri.parse(MOVIEDB_DISCOVER_MOVIE_URL).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                    .appendQueryParameter(PAGE_NUMBER_PARAM, Integer.toString(i + 1))
                    .build();

            new DownloaderTask().execute(builtUri.toString());
        }

    }

    public class DownloaderTask extends AsyncTask<String, JSONObject, List<JSONObject>> {

        private final String LOG_TAG = DownloaderTask.class.getSimpleName();

        @Override
        protected List<JSONObject> doInBackground(String... params) {
            List<JSONObject> objects = new ArrayList<>();

            for (String target : params) {
                try {
                    String json_str = IOUtils.toString(new URL(target).openStream());
                    publishProgress(new JSONObject(json_str));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            return objects;
        }

        @Override
        protected void onProgressUpdate(JSONObject... values) {
            for (JSONObject jsonObject : values) {
                try {
                    JSONArray filmsInfoArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < filmsInfoArray.length(); i++) {
                        JSONObject filmInfo = filmsInfoArray.getJSONObject(i);

                        Bundle bundle = new Bundle();
                        bundle.putInt("id", filmInfo.getInt("id"));
                        bundle.putString("poster", filmInfo.getString("poster_path"));
                        bundle.putString("name", filmInfo.getString("title"));
                        bundle.putString("year", filmInfo.getString("release_date"));
                        bundle.putString("genre", filmInfo.getJSONArray("genre_ids").toString());
                        bundle.putDouble("rating", filmInfo.getDouble("vote_average"));

                        JSONArray genreInfoArray = jsonObject.getJSONArray("genre_ids");
                        for (int j = 0; j < genreInfoArray.length(); j++){
                            JSONObject genreInfo = genreInfoArray.getJSONObject(j);

                            Bundle bundle_genre = new Bundle();
                            bundle_genre.putInt("id", genreInfo.getInt("id"));
                            bundle_genre.putString("name", genreInfo.getString("name"));

                            genreBundeles.add(bundle_genre);
                        }

                        filmBundles.add(bundle);
                    }

                    posterViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
