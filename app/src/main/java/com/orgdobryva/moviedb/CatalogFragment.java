package com.orgdobryva.moviedb;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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


    public CatalogFragment() {
        this.filmBundles = new ArrayList<>();
        this.mDownloaderTasks = Collections.synchronizedList(new ArrayList<CatalogDownloaderTask>());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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


    public void sortByRating() {

        selectedOption = MOVIE_URL_TOP_RATED;
        retrievePages();

    }

    public void sortByPopular() {

        selectedOption = MOVIE_URL_POPULAR;
        retrievePages();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.catalog, menu);
    }

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        setHasOptionsMenu(true);


        if (rootView == null) {
            this.posterViewAdapter = new PosterViewAdapter(getContext(), filmBundles);

            retrievePages();

            rootView = gridView = (GridView) inflater.inflate(R.layout.video_catalog, container, false);
            gridView.setAdapter(posterViewAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bundle details = posterViewAdapter.getItem(position);


                    DetailedFragment detailsFragment = new DetailedFragment();
                    detailsFragment.setArguments(details);

                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    if (getActivity().findViewById(R.id.detailedContainer) == null) {
                        fragmentTransaction.replace(R.id.content_fragment, detailsFragment, "details");
                        fragmentTransaction.addToBackStack("catalog");
                    } else {
                        fragmentTransaction.replace(R.id.detailedContainer, detailsFragment, "details");
                    }
                    fragmentTransaction.commit();


                }
            });
        }

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
}
