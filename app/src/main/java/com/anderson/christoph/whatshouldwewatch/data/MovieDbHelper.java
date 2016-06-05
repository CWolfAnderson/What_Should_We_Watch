package com.anderson.christoph.whatshouldwewatch.data;

import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.anderson.christoph.whatshouldwewatch.data.MovieContract.Review;

/**
 * Manages a local database for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + Review.TABLE_NAME + " (" +

                Review._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                Review.COLUMN_DISPLAY_TITLE + " REAL NOT NULL, " +
                Review.COLUMN_SUMMARY_SHORT + " REAL NOT NULL, " +
                Review.COLUMN_PUBLISH_DATE + " REAL NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Review.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}