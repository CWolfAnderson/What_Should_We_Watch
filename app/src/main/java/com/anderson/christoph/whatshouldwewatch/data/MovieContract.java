package com.anderson.christoph.whatshouldwewatch.data;

import android.provider.BaseColumns;

public class MovieContract {

    public static final class Review implements BaseColumns {

        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_DISPLAY_TITLE = "original_title";
        public static final String COLUMN_SUMMARY_SHORT = "overview";
        public static final String COLUMN_PUBLISH_DATE = "release_date";

    }
}