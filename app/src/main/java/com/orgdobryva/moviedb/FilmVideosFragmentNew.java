package com.orgdobryva.moviedb;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FilmVideosFragmentNew extends Fragment {

    private final String MOVIE_BASE_URL_REVIEWS = "http://api.themoviedb.org/3/movie/";
    private final String APPID_PARAM = "api_key";
    private final String VIDEOS = "videos";

    private LinearLayout mLinearLayout;
    private ScrollView mView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = (ScrollView) inflater.inflate(R.layout.fragment_film_videos, container, false);

        mLinearLayout = (LinearLayout) mView.findViewById(R.id.videos_container);

        Bundle arguments = getArguments();

        if (arguments != null) {

            int id = arguments.getInt("id");

            Uri buildUri = Uri.parse(MOVIE_BASE_URL_REVIEWS).buildUpon()
                    .appendPath(Integer.toString(id))
                    .appendPath(VIDEOS)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                    .build();

            VideosDownLoaderTask videosDownLoaderTask = new VideosDownLoaderTask();
            videosDownLoaderTask.execute(buildUri.toString());
        }

        return mView;
    }

    private class VideosDownLoaderTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {

            List<String> movieKeys = new ArrayList<>();


            try {

                JSONObject object = null;

                try {

                    String json_str = IOUtils.toString(new URL(params[0]).openStream());
                    object = new JSONObject(json_str);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JSONArray videoArray = object.getJSONArray("results");

                for (int i = 0; i < videoArray.length(); i++) {
                    JSONObject key = videoArray.getJSONObject(i);
                    String mkey = key.getString("key");

                    movieKeys.add(mkey);
                    // TODO: STOPS AT ONE VIDEO.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return movieKeys;
        }

        @Override
        protected void onPostExecute(final List<String> movieKeys) {

            for (final String key : movieKeys) {
                YouTubeThumbnailView thumbnailView = new YouTubeThumbnailView(getContext());
                thumbnailView.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubeInitializer(key));
                thumbnailView.setAdjustViewBounds(true);
                thumbnailView.setClickable(true);
                thumbnailView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://www.youtube.com/watch?v=" + key));
                        startActivity(intent);
                    }
                });

                mLinearLayout.addView(thumbnailView, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private class YouTubeInitializer implements YouTubeThumbnailView.OnInitializedListener {

        private final String movieKey;

        public YouTubeInitializer(String movieKey) {
            this.movieKey = movieKey;
        }

        @Override
        public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
            youTubeThumbnailLoader.setVideo(movieKey);
        }

        @Override
        public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {

        }
    }

}


