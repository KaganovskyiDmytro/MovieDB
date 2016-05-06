package com.orgdobryva.moviedb;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by dmytro on 18/04/16.
 */
public class FilmContract extends ContentProvider {

    public static final String CONTENT_AUTHORITY = "com.orgdobryva.moviedb";

    public static final String PATH_ID = "mydb";
    static final String PROVIDER_NAME = CONTENT_AUTHORITY + "." + PATH_ID;
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/favorites");

    // content://com.orgdobryva.moviedb.mydb/favorites


    private SQLiteDatabase db;
    static final String FILMS_TABLE_NAME = "favorites";
    static final String _ID = "_id";
    static final String _FILM_ID = "film_id";


    static final int FILMS = 1;
    static final int FILM_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, FILMS_TABLE_NAME, FILMS);
        uriMatcher.addURI(PROVIDER_NAME, FILMS_TABLE_NAME + "/#", FILM_ID);
    }

    DatabaseFilms dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseFilms(context);

        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(FILMS_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case FILMS:
                // noop
                break;
            case FILM_ID:
                qb.appendWhere(_FILM_ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Log.i("DATABASE", "" + (db == null));

        Cursor filmCursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        filmCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return filmCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uriMatcher.match(uri) == FILMS) {

            long rowID = db.insert(FILMS_TABLE_NAME, "id", values);

            if (rowID > 0) {
                Uri _uri = ContentUris.withAppendedId(BASE_CONTENT_URI, rowID);
                getContext().getContentResolver().notifyChange(_uri, null);

                Log.i("INSERTED", _uri.toString());
                return _uri;
            }

        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return db.delete(FILMS_TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
