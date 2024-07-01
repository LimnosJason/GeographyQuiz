package com.example.geographyquiz;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserDbHelper extends SQLiteOpenHelper
{

    private Context context;
    public static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 1;

    public UserDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Define a string variable to hold the Sql query for table creation. Use UserEntry constants to define the columns
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE "+
                UserContract.UserEntry.TABLE_NAME + " (" +
                UserContract.UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserContract.UserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                UserContract.UserEntry.COLUMN_FLAG_SCORE + " INT NOT NULL, " +
                UserContract.UserEntry.COLUMN_OUTLINE_SCORE + " INT NOT NULL, " +
                UserContract.UserEntry.COLUMN_PICTURE + " TEXT, " +
                UserContract.UserEntry.COLUMN_SELECTED + " BOOLEAN NOT NULL," +
                UserContract.UserEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        //Use execSQL() method on the db object to execute the sql query
        sqLiteDatabase.execSQL(SQL_CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //Define a String variable to hold the sql query for dropping all the tables within the DB
        String SQL_DROP_DB = "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

        //Use execSQL() method on the db object to execute the sql query
        sqLiteDatabase.execSQL(SQL_DROP_DB);

        //Call onCreate() to recreate the database with all (and new) tables
        onCreate(sqLiteDatabase);
    }
}
