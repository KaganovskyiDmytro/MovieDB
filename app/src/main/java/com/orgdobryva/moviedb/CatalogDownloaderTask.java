package com.orgdobryva.moviedb;

import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class CatalogDownloaderTask extends AsyncTask<String, Bundle, Void> {

    private final String LOG_TAG = CatalogDownloaderTask.class.getSimpleName();

    private CatalogFragment mCatalogFragment;

    public CatalogDownloaderTask(CatalogFragment catalogFragment) {
        mCatalogFragment = catalogFragment;
    }

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

        mCatalogFragment.getFilmBundles().addAll(Arrays.asList(values));
        mCatalogFragment.getPosterViewAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Void value) {
        mCatalogFragment.getDownloaderTasks().remove(this);
    }
}
