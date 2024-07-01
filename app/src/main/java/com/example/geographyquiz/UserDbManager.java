package com.example.geographyquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDbManager {

    private UserDbHelper mDbHelper;
    private SQLiteDatabase mDatabase;

    private static UserDbManager sInstance;

    private final static String SELECT_ALL_QUERY = "SELECT * from " +
            UserContract.UserEntry.TABLE_NAME;

    private final static String SELECT_SELECTED_QUERY = "SELECT * from " + UserContract.UserEntry.TABLE_NAME +
            " WHERE " + UserContract.UserEntry.COLUMN_SELECTED + " = " +1;

    private UserDbManager(Context context) {
        mDbHelper = new UserDbHelper(context);
    }

    public static synchronized UserDbManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new UserDbManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public boolean openWritableDatabase() {
        mDatabase = mDbHelper.getWritableDatabase();
        return mDatabase != null;
    }

    public boolean openReadableDatabase() {
        mDatabase = mDbHelper.getReadableDatabase();
        return mDatabase != null;
    }

    public void closeDatabase() {
        mDbHelper.close();
    }

    public long createUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(UserContract.UserEntry.COLUMN_NAME, user.getUserName());
        cv.put(UserContract.UserEntry.COLUMN_FLAG_SCORE, user.getUserFlagScore());
        cv.put(UserContract.UserEntry.COLUMN_OUTLINE_SCORE, user.getUserOutlineScore());
        cv.put(UserContract.UserEntry.COLUMN_PICTURE, user.getUserPicturePath());
        cv.put(UserContract.UserEntry.COLUMN_SELECTED, user.isSelected());

        return mDatabase.insert(UserContract.UserEntry.TABLE_NAME, null, cv);
    }

    public Cursor readUsers() {
        return mDatabase.rawQuery(SELECT_ALL_QUERY, null);
    }

    public Cursor readSelectedUser() {
        return mDatabase.rawQuery(SELECT_SELECTED_QUERY, null);
    }

    public Cursor readFlagScoresSorted() {
        Cursor cursor = mDatabase.rawQuery(SELECT_ALL_QUERY + " ORDER BY "+
                UserContract.UserEntry.COLUMN_FLAG_SCORE + " DESC", null);
        return cursor;
    }

    public Cursor readOutlineScoresSorted() {
        Cursor cursor = mDatabase.rawQuery(SELECT_ALL_QUERY + " ORDER BY "+
                UserContract.UserEntry.COLUMN_OUTLINE_SCORE + " DESC", null);
        return cursor;
    }


    //Create a ContentValues object and fill it with the user data for each table column
    public long updateUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(UserContract.UserEntry.COLUMN_NAME, user.getUserName());
        cv.put(UserContract.UserEntry.COLUMN_FLAG_SCORE, user.getUserFlagScore());
        cv.put(UserContract.UserEntry.COLUMN_OUTLINE_SCORE, user.getUserOutlineScore());
        cv.put(UserContract.UserEntry.COLUMN_PICTURE, user.getUserPicturePath());
        cv.put(UserContract.UserEntry.COLUMN_SELECTED, user.isSelected());

        return mDatabase.update(
                UserContract.UserEntry.TABLE_NAME,
                cv,
                UserContract.UserEntry._ID + "=" + user.getId(),
                null);
    }

    //Delete user
    public boolean deleteUser(long id) {
        boolean res = mDatabase.delete(
                UserContract.UserEntry.TABLE_NAME,
                UserContract.UserEntry._ID + "=" + id,
                null) > 0;
        return res;
    }
}
