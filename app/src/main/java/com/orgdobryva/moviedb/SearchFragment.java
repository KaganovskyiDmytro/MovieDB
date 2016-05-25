package com.orgdobryva.moviedb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by dmytro on 22/05/16.
 */
public class SearchFragment extends ListFragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String searchUri = getArguments().getString("searchUri");

        SearchDownloaderTask searchDownloaderTask = new SearchDownloaderTask();
        try {
            List<Map<String, Object>> maps = searchDownloaderTask.execute(searchUri).get();

            Log.i("SEARCH", "COUNT: " + maps.size());

            String [] from = {
                    "title"
            };

            int [] to = {
                    android.R.id.text1
            };

            SimpleAdapter adapter = new SimpleAdapter(getContext(), maps, android.R.layout.simple_list_item_1,
                   from, to);

            setListAdapter(adapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_search, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
