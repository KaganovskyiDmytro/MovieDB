package com.orgdobryva.moviedb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogFragment extends Fragment {

    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String MOVIE_URL_TOP_RATED = "top_rated";
    private final String MOVIE_URL_POPULAR = "popular";

    private String selectedOption = MOVIE_URL_TOP_RATED;

    private final String APPID_PARAM = "api_key";
    private final String PAGE_NUMBER_PARAM = "page";

    private List<CatalogDownloaderTask> mDownloaderTasks;

    private List<Bundle> filmBundles;
    private PosterViewAdapter posterViewAdapter;
    private GridView gridView;
    private Callbacks mCallbacks;


    public CatalogFragment() {
        this.filmBundles = new ArrayList<>();
        this.mDownloaderTasks = Collections.synchronizedList(new ArrayList<CatalogDownloaderTask>());
    }



    public PosterViewAdapter getPosterViewAdapter() {
        return posterViewAdapter;
    }

    public List<Bundle> getFilmBundles() {
        return filmBundles;
    }

    public List<CatalogDownloaderTask> getDownloaderTasks() {
        return mDownloaderTasks;
    }

    public interface Callbacks{
        void onFilmSelected (int position);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        this.posterViewAdapter = new PosterViewAdapter(getContext(), filmBundles);

        retrievePages();

        gridView = (GridView) inflater.inflate(R.layout.video_catalog, container, false);
        gridView.setAdapter(posterViewAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle details = posterViewAdapter.getItem(position);


//                FilmDetailsFragment detailsFragment = new FilmDetailsFragment();
//                detailsFragment.setArguments(details);

//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.content_fragment, detailsFragment);
//                fragmentTransaction.addToBackStack("catalog");
//                fragmentTransaction.commit();
                Intent intent = new Intent(getActivity(), DetailedActivity.class);
                intent.putExtra("details", details);
                startActivity(intent);

            }
        });

        return gridView;
    }


    private void retrievePages() {
        for (CatalogDownloaderTask task : mDownloaderTasks) {
            task.cancel(true);
        }

        filmBundles.clear();
        posterViewAdapter.notifyDataSetChanged();


        for (int i = 0; i < 10; i++) {
            Uri builtUri = Uri.parse(String.format("%s%s?", MOVIE_BASE_URL, selectedOption)).buildUpon()
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                    .appendQueryParameter(PAGE_NUMBER_PARAM, Integer.toString(i + 1))
                    .build();

            CatalogDownloaderTask downloaderTask = new CatalogDownloaderTask(this);
            mDownloaderTasks.add(downloaderTask);
            downloaderTask.execute(builtUri.toString());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}
