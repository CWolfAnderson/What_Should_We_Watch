package com.anderson.christoph.whatshouldwewatch;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.anderson.christoph.whatshouldwewatch.data.MovieContract;
import com.anderson.christoph.whatshouldwewatch.data.MovieDbHelper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    private static final String TEST_MOVIE = "Deadpool";
    private static final String TEST_DATE = "2016-02-09";
    private static final String TEST_DESCRIPTION = "Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL tells the origin story of former Special Forces operative turned mercenary Wade Wilson, who after being subjected to a rogue experiment that leaves him with accelerated healing powers, adopts the alter ego Deadpool. Armed with his new abilities and a dark, twisted sense of humor, Deadpool hunts down the man who nearly destroyed his life.";

    // since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    // called before each test is executed to delete the database, makes sure that we always have a clean test
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        // build a HashSet of all of the table names we wish to look for
        final HashSet<String> tableNameHashSet = new HashSet<>();

        tableNameHashSet.add(MovieContract.Review.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);

        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.Review.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        final HashSet<String> locationColumnHashSet = new HashSet<>();
        locationColumnHashSet.add(MovieContract.Review._ID);
        locationColumnHashSet.add(MovieContract.Review.COLUMN_DISPLAY_TITLE);
        locationColumnHashSet.add(MovieContract.Review.COLUMN_SUMMARY_SHORT);
        locationColumnHashSet.add(MovieContract.Review.COLUMN_PUBLISH_DATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testReviewTable() {
// First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.Review.COLUMN_DISPLAY_TITLE, TEST_MOVIE);
        testValues.put(MovieContract.Review.COLUMN_SUMMARY_SHORT, TEST_DESCRIPTION);
        testValues.put(MovieContract.Review.COLUMN_PUBLISH_DATE, TEST_DATE);

        // Third Step: Insert ContentValues into database and get a row ID back
        long movieRowId = db.insert(MovieContract.Review.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                MovieContract.Review.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back from the query
        assertTrue("Error: No Records returned from location query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Review.TABLE_NAME);
        db.close();

    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }
}