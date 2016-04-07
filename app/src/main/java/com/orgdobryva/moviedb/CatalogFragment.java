package com.orgdobryva.moviedb;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CatalogFragment extends Fragment {

    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String MOVIE_URL_TOP_RATED = "top_rated";
    private final String MOVIE_URL_POPULAR = "popular";

    private String selectedOption = MOVIE_URL_TOP_RATED;

    private final String APPID_PARAM = "api_key";
    private final String PAGE_NUMBER_PARAM = "page";

    private List<DownloaderTask> mDownloaderTasks;

    private List<Bundle> filmBundles;
    private NewPosterViewAdapter posterViewAdapter;


    public CatalogFragment() {
        this.filmBundles = new ArrayList<>();
        this.mDownloaderTasks = Collections.synchronizedList(new ArrayList<DownloaderTask>());
    }

    public void sortByRating() {

        selectedOption = MOVIE_URL_TOP_RATED;
        retrievePages();

    }

    public void sortByPopular() {

        selectedOption = MOVIE_URL_POPULAR;
        retrievePages();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.video_catalog, container, false);

        this.posterViewAdapter = new NewPosterViewAdapter(getContext(), filmBundles);

        retrievePages();

        GridView gridView = (GridView) layout.findViewById(R.id.catalogGridView);
        gridView.setAdapter(posterViewAdapter);

        return layout;
    }

    private void retrievePages() {
        for (DownloaderTask task : mDownloaderTasks) {
            task.cancel(true);
        }

        filmBundles.clear();
        posterViewAdapter.notifyDataSetChanged();


        for (int i = 0; i < 10; i++) {
            Uri builtUri = Uri.parse(String.format("%s%s?", MOVIE_BASE_URL, selectedOption)).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                    .appendQueryParameter(PAGE_NUMBER_PARAM, Integer.toString(i + 1))
                    .build();

            DownloaderTask downloaderTask = new DownloaderTask();
            mDownloaderTasks.add(downloaderTask);
            downloaderTask.execute(builtUri.toString());
        }

    }

    public class DownloaderTask extends AsyncTask<String, Bundle, Void> {

        private final String LOG_TAG = DownloaderTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {
            for (String target : params) {
                JSONObject jsonObject = null;

                try {
                    String json_str = IOUtils.toString(new URL(target).openStream());
                    jsonObject = new JSONObject(json_str);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObject != null) {
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

                            publishProgress(bundle);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Bundle... values) {
            filmBundles.addAll(Arrays.asList(values));
            posterViewAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void value) {
            mDownloaderTasks.remove(this);
        }
    }


}
