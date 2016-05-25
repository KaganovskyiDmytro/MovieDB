package com.orgdobryva.moviedb;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SearchEvent;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private int current_page = 1;
    private CatalogFragment mCatalogFragment = new CatalogFragment();
    private FavoritesFragment mFavoritesFragment = new FavoritesFragment();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("SEARCH", "!!! STARTS");
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.i("SEARCH", "!!! PUSH QUERY: " + query);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("current_page", current_page);
            mCatalogFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_fragment, mCatalogFragment, "catalog")
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onSearchRequested() {
        Log.i("SEARCH", "Request!");
        return super.onSearchRequested();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        ActionBar supportActionBar = getActionBar();
        switch (fragment.getTag()) {
            case "favorites":
                supportActionBar.setTitle("Favorites");
                break;

            case "catalog":
                supportActionBar.setTitle("Catalog");
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

//            case R.id.menu_search:
//
//                break;


            case R.id.sort_by_popularity:


                item.setChecked(true);
                if (mCatalogFragment.isVisible()) {
                    mCatalogFragment.sortByPopular();
                }


                break;

            case R.id.sort_by_reit:

                item.setChecked(true);
                if (mCatalogFragment.isVisible()) {
                    mCatalogFragment.sortByRating();
                }

                break;


        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fm = getSupportFragmentManager();

        switch (item.getItemId()) {

            case R.id.nav_favorites:

                item.setChecked(true);

                fm.beginTransaction().replace(R.id.content_fragment, mFavoritesFragment, "favorites")
                        .addToBackStack("catalog").commit();

                break;

            case R.id.nav_films:

                item.setChecked(true);
                fm.beginTransaction().replace(R.id.content_fragment, mCatalogFragment, "catalog")
                        .commit();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
