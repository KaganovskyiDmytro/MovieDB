package com.orgdobryva.moviedb;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;

public class DetailedFragment extends Fragment {

    private final String BASE_URL_ADDRESS = "https://www.themoviedb.org/movie/";
    private  String buildSharing;

    boolean isFavorite = false;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_film_details, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.activity_detailed, container, false);

        final Bundle details = getArguments();


        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), details);

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        Uri build = FilmContract.BASE_CONTENT_URI.buildUpon().appendEncodedPath("" + details.getInt("id")).build();
        Log.i("BUILDED URI", build.toString());

        buildSharing = BASE_URL_ADDRESS.concat(String.valueOf(details.getInt("id")));

        Cursor fileCursor = getActivity().getContentResolver().query(build, null, null, null, null);

        Log.i("CURSOR = ", fileCursor.toString());

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.favorite_fab);

        if (fileCursor.getCount() > 0) {
            isFavorite = true;

            fab.setImageResource(android.R.drawable.star_big_on);
            fab.show();
        }
        fileCursor.close();


        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Bundle details = getArguments();

                isFavorite = !isFavorite;

                if (isFavorite) {
                    fab.setImageResource(android.R.drawable.star_big_on);
                    fab.show();

                    ContentValues cv = new ContentValues();
                    cv.put("film_id", details.getInt("id"));
                    cv.put("film_name", details.getString("name"));


                    Bitmap posterBitmap = mSectionsPagerAdapter.getDetailsFragment().getPosterBitmap();

                    ByteBuffer bb = ByteBuffer.allocate(posterBitmap.getByteCount());

                    posterBitmap.copyPixelsToBuffer(bb);

                    cv.put("film_image", bb.array());

                    Log.i("CONTENT VALUE", cv.toString());

                    getActivity().getContentResolver().insert(FilmContract.BASE_CONTENT_URI, cv);
                } else {
                    fab.setImageResource(android.R.drawable.star_big_off);
                    fab.show();

                    getActivity().getContentResolver().delete(FilmContract.BASE_CONTENT_URI, "film_id=?",
                            new String[]{Integer.toString(details.getInt("id"))});
                }
            }
        });

        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_settings:

                break;

            case R.id.film_menu_share:

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, buildSharing);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, buildSharing));

                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detailed, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Bundle filmDetails;
        private FilmDetailsFragment mDetailsFragment;
        private FilmVideosFragment mVideosFragment;
        private FilmReviewsFragment mReviewsFragment;

        public SectionsPagerAdapter(FragmentManager fm, Bundle filmDetails) {
            super(fm);
            this.filmDetails = filmDetails;

        }

        public FilmDetailsFragment getDetailsFragment() {
            return mDetailsFragment;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (mDetailsFragment == null) {
                        mDetailsFragment = new FilmDetailsFragment();
                        mDetailsFragment.setArguments(filmDetails);
                    }
                    return mDetailsFragment;
                case 1:
                    if (mVideosFragment == null) {
                        mVideosFragment = new FilmVideosFragment();
                        mVideosFragment.setArguments(filmDetails);
                    }
                    return mVideosFragment;

                case 2:
                    if (mReviewsFragment == null) {
                        mReviewsFragment = new FilmReviewsFragment();
                        mReviewsFragment.setArguments(filmDetails);
                    }
                    return mReviewsFragment;
            }

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {

            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Description";
                case 1:
                    return "Video";
                case 2:
                    return "Reviews";
            }
            return null;
        }
    }
}
