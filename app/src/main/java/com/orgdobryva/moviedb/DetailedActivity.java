package com.orgdobryva.moviedb;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailedActivity extends AppCompatActivity {

    boolean isFavorite = false;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        getActionBar().setNavigationMode();

        final Bundle details = getIntent().getBundleExtra("details");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), details);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        Uri build = FilmContract.BASE_CONTENT_URI.buildUpon().appendEncodedPath("" + details.getInt("id")).build();
        Log.i("BUILDED URI", build.toString());

        Cursor fileCursor = getContentResolver().query(build, null, null, null, null);

        Log.i("CURSOR = ", fileCursor.toString());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.favorite_fab);

        if (fileCursor.getCount() > 0) {
            isFavorite = true;

            fab.setImageResource(android.R.drawable.star_big_on);
            fab.show();
        }
        fileCursor.close();


        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


                Bundle details = getIntent().getBundleExtra("details");

                isFavorite = !isFavorite;

                if (isFavorite) {
                    fab.setImageResource(android.R.drawable.star_big_on);
                    fab.show();

                    ContentValues cv = new ContentValues();
                    cv.put("film_id", details.getInt("id"));
                    cv.put("film_name", details.getString("name"));

                    Log.i("CONTENT VALUE", cv.toString());

                    getContentResolver().insert(FilmContract.BASE_CONTENT_URI, cv);
                } else {
                    fab.setImageResource(android.R.drawable.star_big_off);
                    fab.show();

                    getContentResolver().delete(FilmContract.BASE_CONTENT_URI, "film_id=?",
                            new String[]{Integer.toString(details.getInt("id"))});
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private Bundle filmDetails;
        private FilmDetailsFragment mDetailsFragment;
        private FilmVideosFragmentNew mVideosFragment;
        private FilmReviewsFragment mReviewsFragment;

        public SectionsPagerAdapter(FragmentManager fm, Bundle filmDetails) {
            super(fm);
            this.filmDetails = filmDetails;

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
                        mVideosFragment = new FilmVideosFragmentNew();
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


            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
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
