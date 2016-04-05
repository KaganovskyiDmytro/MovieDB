package com.orgdobryva.moviedb;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by human on 3/30/16.
 */
public class PosterViewAdapter extends ArrayAdapter<Bundle> {

    private final int MINIMUM_VIEWS = 12;
    private int current = 0;
    private ImageCacher imageCacher;

    public PosterViewAdapter(Context context, List<Bundle> bundles) {
        super(context, 0, bundles);
        this.imageCacher = new ImageCacher();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Bundle bundle = getItem(position);

        PosterView posterView;

        if (convertView == null || current < MINIMUM_VIEWS) {
            current++;

            posterView = new PosterView(getContext());
            posterView.setCacher(imageCacher);
        } else {
            posterView = (PosterView) convertView;
        }

        posterView.setBundle(bundle);

        return posterView;
    }
}
