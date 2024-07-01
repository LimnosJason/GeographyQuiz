package com.example.geographyquiz;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;

public class ScoreActivity extends AppCompatActivity {
    private TextView tittleTv;
    private TextView highScoreTv;
    private TextView currentScoreTittleTv;
    private TextView currentScoreTv;
    private TextView newHighScoreTv;
    private TextView mUserNameTv;
    private ImageView backArrowIv;
    private ImageView mUserPictureIv;

    private UserDbManager dbManager;

    private User user;
    private String score;
    private String tittle;
    private String correctAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        //Find all views in activities
        tittleTv = findViewById(R.id.tv_tittle);
        highScoreTv = findViewById(R.id.tv_high_score);
        currentScoreTittleTv = findViewById(R.id.tv_current_score_tittle);
        currentScoreTv = findViewById(R.id.tv_current_score);
        newHighScoreTv = findViewById(R.id.tv_new_high_score);
        mUserNameTv = findViewById(R.id.tv_user_name);
        backArrowIv = findViewById(R.id.iv_back_arrow);
        mUserPictureIv = findViewById(R.id.iv_user_picture);

        newHighScoreTv.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        score = intent.getExtras().getString("score");
        tittle = intent.getExtras().getString("tittle");
        correctAnswer = intent.getExtras().getString("correctAnswer");

        //Snack bar for the correct answer on the question the player got wrong
        Snackbar.make(findViewById(android.R.id.content), "Correct answer was : " + correctAnswer, Snackbar.LENGTH_LONG).show();

        tittleTv.setText(tittle);
        currentScoreTv.setText(score);

        //setup database manager
        dbManager = UserDbManager.getInstance(getApplicationContext());

        new GetSelectedUserTask().execute();

        //back button pressed finish activity
        backArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //get current selected user
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
                    if(tittle.equals("Flag High Score")){
                        //if its a high score
                        if(Integer.parseInt(score)>user.getUserFlagScore()){
                            //save the new flag score on the selected user
                            user.setUserFlagScore(Integer.parseInt(score));
                            if (dbManager != null && dbManager.openWritableDatabase()) {
                                dbManager.updateUser(user);
                            }
                            //animate all text views if its a new high score
                            animateIt(tittleTv);
                            animateIt(highScoreTv);
                            animateIt(currentScoreTittleTv);
                            animateIt(currentScoreTv);
                            animateIt(newHighScoreTv);
                            newHighScoreTv.setVisibility(View.VISIBLE);
                        }
                        highScoreTv.setText(String.valueOf(user.getUserFlagScore()));
                    }
                    else{
                        //if its a high score
                        if(Integer.parseInt(score)>user.getUserOutlineScore()){
                            //save the new outline score on the selected user
                            user.setUserOutlineScore(Integer.parseInt(score));
                            if (dbManager != null && dbManager.openWritableDatabase()) {
                                dbManager.updateUser(user);
                            }
                            //animate all text views if its a new high score
                            animateIt(tittleTv);
                            animateIt(highScoreTv);
                            animateIt(currentScoreTittleTv);
                            animateIt(currentScoreTv);
                            animateIt(newHighScoreTv);
                            newHighScoreTv.setVisibility(View.VISIBLE);
                        }
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
    //set image and username
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
    //animation of texts views
    public void animateIt(TextView tv){
        ObjectAnimator a = ObjectAnimator.ofInt(tv, "textColor", Color.GREEN, Color.RED);
        a.setInterpolator(new LinearInterpolator());
        a.setDuration(1500);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.setEvaluator(new ArgbEvaluator());
        AnimatorSet t = new AnimatorSet();
        t.play(a);
        t.start();
    }
}