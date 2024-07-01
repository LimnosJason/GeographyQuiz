package com.example.geographyquiz;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class HighScoreActivity extends AppCompatActivity {
    private TextView tittleTv;
    private TextView highScoreTv;
    private TextView mUserNameTv;
    private ImageView backArrowIv;
    private ImageView mUserPictureIv;

    private HighScoreRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mHighScoreRecyclerView;

    private UserDbManager dbManager;

    private Cursor mCursor;
    private User user;
    private String tittle;
    private boolean isFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //Find all views in activities
        tittleTv =  findViewById(R.id.tv_tittle);
        highScoreTv =  findViewById(R.id.tv_high_score);
        mUserNameTv =  findViewById(R.id.tv_user_name);
        backArrowIv = findViewById(R.id.iv_back_arrow);
        mUserPictureIv = findViewById(R.id.iv_user_picture);

        mHighScoreRecyclerView = findViewById(R.id.rv_high_score);

        //setup database manager
        dbManager = UserDbManager.getInstance(getApplicationContext());

        Intent intent = getIntent();

        tittle = intent.getExtras().getString("tittle");
        isFlag = intent.getExtras().getBoolean("isFlag");

        tittleTv.setText(tittle);

        populateList();
        new GetSelectedUserTask().execute();
        backArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //populate recycler view
    private void populateList() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mHighScoreRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mHighScoreRecyclerView.getContext(),
                layoutManager.getOrientation());
        mHighScoreRecyclerView.addItemDecoration(mDividerItemDecoration);
        mHighScoreRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new HighScoreRecyclerAdapter(this, mCursor,isFlag);
        mHighScoreRecyclerView.setAdapter(mRecyclerAdapter);

        new GetHighScoresTask().execute();
    }

    private class GetHighScoresTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            // check if dbManager is initialized and get a readable database
            if (dbManager != null && dbManager.openReadableDatabase()) {
                //get all the users and return the resulted cursor
                if(isFlag){
                    return dbManager.readFlagScoresSorted();
                }
                else{
                    return dbManager.readOutlineScoresSorted();
                }
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            //check if the resulted cursor is not empty
            if (cursor != null) {
                //update the current cursor with the new one
                mCursor = cursor;
                mRecyclerAdapter.setCursor(cursor);
            }
        }
    }

    private class GetSelectedUserTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            // check if dbManager is initialized and get a readable database
            if (dbManager != null && dbManager.openReadableDatabase()) {
                //get all the users and return the resulted cursor
                return dbManager.readSelectedUser();
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null) {
                try{
                    cursor.moveToFirst();
                    user = populateUser(cursor);
                    //if flag high score was pressed
                    if(tittle.equals("Flag High Score")){
                        highScoreTv.setText(String.valueOf(user.getUserFlagScore()));
                    }
                    //if country high score was pressed
                    else{
                        highScoreTv.setText(String.valueOf(user.getUserOutlineScore()));
                    }
                    mUserNameTv.setText(user.getUserName());
                    String imagePath =cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PICTURE));

                    //check if the path is null or empty
                    if (imagePath != null && !imagePath.equals("")) {// path exists
                        //use Picasso library to load the image from the specified location
                        Picasso.get()
                                .load(new File(imagePath))
                                .fit().centerInside()
                                .placeholder(R.drawable.img_contact_logo)
                                .error(R.drawable.img_contact_logo)
                                .into(mUserPictureIv);
                    } else {//path is empty or null
                        //use Picasso library to load the default user image from the application resources
                        Picasso.get()
                                .load(R.drawable.img_contact_logo)
                                .fit().centerInside()
                                .into(mUserPictureIv);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //populate information of current selected user
    private User populateUser(Cursor cursor){
        user = new User();
        user.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME)));
        user.setUserPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PICTURE)));
        user.setUserFlagScore(cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_FLAG_SCORE)));
        user.setUserOutlineScore(cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_OUTLINE_SCORE)));
        user.setId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID))));
        user.setSelected(true);
        return user;
    }
}