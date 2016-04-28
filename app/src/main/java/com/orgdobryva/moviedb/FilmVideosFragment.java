package com.orgdobryva.moviedb;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FilmVideosFragment extends Fragment implements YouTubePlayer.OnInitializedListener {

    private final String MOVIE_BASE_URL_REVIEWS = "http://api.themoviedb.org/3/movie/";
    private final String APPID_PARAM = "api_key";
    private final String VIDEOS = "videos";

    private ArrayList<String> videoList = new ArrayList<>();

    private YouTubePlayerSupportFragment youTubeView;

    private String movieKey;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_film_videos, container, false);

//        youTubeView = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_view);

        Log.i("ON ACTIVITY CREATED", "" + youTubeView);

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

        return view;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

        Log.i("MOVIE KEY:", movieKey);

        if (!wasRestored) {
            player.cueVideo(movieKey); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(getActivity(), 1).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        }
    }

    private class VideosDownLoaderTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

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

                Bundle bundle = new Bundle();

                JSONArray videoArray = object.getJSONArray("results");

                for (int i = 0; i < videoArray.length(); i++) {
                    JSONObject key = videoArray.getJSONObject(i);
                    String movieKey = key.getString("key");

                    return movieKey;

                    // TODO: STOPS AT ONE VIDEO.
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String movieKey) {
            FilmVideosFragment.this.movieKey = movieKey;

//            Log.i("POST EXECUTE: ", movieKey + "%" + BuildConfig.YOUTUBE_API_KEY + ":" + FilmVideosFragment.this + "#" + youTubeView);

            youTubeView.initialize(BuildConfig.YOUTUBE_API_KEY, FilmVideosFragment.this);
        }
    }
}
