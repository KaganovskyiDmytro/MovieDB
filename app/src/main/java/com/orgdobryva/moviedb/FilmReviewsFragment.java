package com.orgdobryva.moviedb;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmReviewsFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private final String MOVIE_BASE_URL_REVIEWS = "http://api.themoviedb.org/3/movie/";
    private final String APPID_PARAM = "api_key";
    private final String REVIEW = "reviews";

    private List<Map<String, Object>> reviewsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_film_reviews, container, false);

        Bundle arguments = getArguments();

        if (arguments != null) {

            int id = arguments.getInt("id");

            if (reviewsList.isEmpty()){
                Uri  buildUri = Uri.parse(MOVIE_BASE_URL_REVIEWS).buildUpon()
                    .appendPath(Integer.toString(id))
                    .appendPath(REVIEW)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                    .build();


                ReviewsDownloaderTask task = new ReviewsDownloaderTask();
                task.execute(buildUri.toString());
            }

            String [] from = {
                    "author", "content"
            };
            int [] to = {
                    R.id.reviews_author, R.id.reviews_content
            };

            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), reviewsList,
                    R.layout.reviews_list_item, from, to);
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
//                    android.R.layout.simple_list_item_2, reviews);

            setListAdapter(simpleAdapter);
        }

        return  view;

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private class ReviewsDownloaderTask extends AsyncTask<String, Map<String, Object>, Void>{

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
                        JSONArray reviewArray = jsonObject.getJSONArray("results");

                        for (int i = 0; i < reviewArray.length(); i++) {
                            JSONObject reviewInfo = reviewArray.getJSONObject(i);

                            Map<String, Object> reviewMap = new HashMap<>();
                            reviewMap.put("author", reviewInfo.getString("author"));

                            String content = reviewInfo.getString("content");

                            if (true) {
                                reviewMap.put("content", content);
                            } else {
                                reviewMap.put("content", content.substring(0, 100));
                            }


                            publishProgress(reviewMap);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Map<String, Object>... values) {
            reviewsList.add(values[0]);

            ((SimpleAdapter)getListAdapter()).notifyDataSetChanged();
        }
    }
}
