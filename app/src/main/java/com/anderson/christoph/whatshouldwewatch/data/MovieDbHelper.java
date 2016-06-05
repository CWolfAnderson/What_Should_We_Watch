package com.anderson.christoph.whatshouldwewatch.data;

import android.database.sqlite.SQLiteOpenHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;

import com.anderson.christoph.whatshouldwewatch.data.MovieContract.Review;

/**
 * Manages a local database for movie data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + Review.TABLE_NAME + " (" +
                // Unique keys will be auto-generated in either case.  But for movie
                // reviews, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                Review._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                Review.COLUMN_SUMMARY_SHORT + "REAL NOT NULL, " +
                Review.COLUMN_PUBLISH_DATE + "REAL NOT NULL, " +

                // the ID of the location entry associated with this weather data
                Review.COLUMN_DISPLAY_TITLE + " INTEGER NOT NULL;";

//                Review.COLUMN_DATE + " INTEGER NOT NULL, " +
//                Review.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
//                Review.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
//
//                Review.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
//                Review.COLUMN_MAX_TEMP + " REAL NOT NULL, " +
//
//                Review.COLUMN_HUMIDITY + " REAL NOT NULL, " +
//                Review.COLUMN_PRESSURE + " REAL NOT NULL, " +
//                Review.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
//                Review.COLUMN_DEGREES + " REAL NOT NULL, " +

//                // Set up the location column as a foreign key to location table.
//                " FOREIGN KEY (" + Review.COLUMN_LOC_KEY + ") REFERENCES " +
//                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
//                " UNIQUE (" + Review.COLUMN_DATE + ", " +
//                Review.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

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
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Review.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}