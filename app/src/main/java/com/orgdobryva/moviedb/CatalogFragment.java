package com.orgdobryva.moviedb;

import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CatalogFragment extends Fragment implements SearchView.OnQueryTextListener {

    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String MOVIE_CHOICE = "http://api.themoviedb.org/3/search/movie";
    private final String MOVIE_URL_TOP_RATED = "top_rated";
    private final String MOVIE_URL_POPULAR = "popular";

    private String selectedOption = MOVIE_URL_TOP_RATED;

    private final String APPID_PARAM = "api_key";
    private final String PAGE_NUMBER_PARAM = "page";

    private List<CatalogDownloaderTask> mDownloaderTasks;
    private List<SearchDownloaderTask> mSearchDownloaderTasks;

    private List<Bundle> filmBundles;
    private PosterViewAdapter posterViewAdapter;
    private GridView gridView;

    private SimpleCursorAdapter mCursorAdapter;


    public CatalogFragment() {
        this.filmBundles = new ArrayList<>();
        this.mDownloaderTasks = Collections.synchronizedList(new ArrayList<CatalogDownloaderTask>());
        this.mSearchDownloaderTasks = Collections.synchronizedList(new ArrayList<SearchDownloaderTask>());
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

    public List<SearchDownloaderTask> getSearchDownloaderTasks() {
        return mSearchDownloaderTasks;
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

        // Associate searchable configuration with the SearchView
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSuggestionsAdapter(mCursorAdapter);
        searchView.setOnQueryTextListener(this);
//        searchView.setBackgroundColor(Color.DKGRAY);

    }

    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] from = {"title"};

        int[] to = {android.R.id.text1};

        mCursorAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_1,
                null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        if (rootView == null) {
            this.posterViewAdapter = new PosterViewAdapter(getContext(), filmBundles);

            retrievePages();

            rootView = inflater.inflate(R.layout.video_catalog, container, false);

            gridView = (GridView) rootView.findViewById(R.id.catalogGridView);
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


    @Override
    public boolean onQueryTextSubmit(String query) {

        Uri builtSearch = Uri.parse(MOVIE_CHOICE).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                .appendQueryParameter("query", query.toString())
                .build();

        Bundle search = new Bundle();
        search.putString("searchUri", builtSearch.toString());

        SearchFragment searchFragment = new SearchFragment();
        searchFragment.setArguments(search);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, searchFragment, "search");
        fragmentTransaction.commit();

        Log.i("SEARCH", "SUBMIT: " + query + " " + builtSearch);


        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.i("SEARCH", "INPUT: " + query);
        Uri builtSearch = Uri.parse(MOVIE_CHOICE).buildUpon()
                .appendQueryParameter(APPID_PARAM, BuildConfig.THE_MOVIEDB_API_KEY)
                .appendQueryParameter("query", query.toString())
                .build();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{
                BaseColumns._ID, "title"
        });

        SearchDownloaderTask searchDownloaderTask = new SearchDownloaderTask();
        try {
            List<Map<String, Object>> maps = searchDownloaderTask.execute(builtSearch.toString()).get();

            for (Map<String,Object> item:  maps) {
                matrixCursor.addRow(new Object[] {item.get("id"), item.get("title")});
            }

            Log.i("SEARCH", "COUNT: " + maps.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }



        mCursorAdapter.changeCursor(matrixCursor);

        return true;
    }


}
