package com.example.geographyquiz;

import android.provider.BaseColumns;

public class UserContract {

    public static final class UserEntry implements BaseColumns {

        //String constant for the actual table name
        public static final String TABLE_NAME = "Users";

        //String constants for the actual column names
        public static final String COLUMN_NAME = "user";
        public static final String COLUMN_FLAG_SCORE = "flag_high_score";
        public static final String COLUMN_OUTLINE_SCORE = "outline_high_score";
        public static final String COLUMN_PICTURE = "pictureUri";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_SELECTED = "selected";
    }

}
