package com.example.geographyquiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mFlagBtn;
    private Button mCountryBtn;
    private ImageView mFlagScoreIv;
    private ImageView mOutlineScoreIv;
    private ImageView mUser;

    private Cursor mCursor;
    private UserDbManager dbManager;

    private ObjectAnimator userIvAnimator;
    private AnimatorSet userIvAnimatorSet;

    private static final int SELECTED_USER_REQ_CODE = 240;
    private boolean noSelectedUserFlag=false;
    private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find all views in activities
        mFlagBtn = findViewById(R.id.btn_flag);
        mCountryBtn = findViewById(R.id.btn_country);
        mFlagScoreIv = findViewById(R.id.iv_flag_score);
        mOutlineScoreIv = findViewById(R.id.iv_outline_score);
        mUser = findViewById(R.id.iv_user);

        //setup database manager
        dbManager = UserDbManager.getInstance(getApplicationContext());

        //Animate the user button
        animateIt();

        //OnClick for the flag quiz button
        mFlagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=true;
                //If the user has no users selected
                if(!noSelectedUserFlag) {
                    showNoSelectedUserDialog();
                }
                //Go to the quiz activity
                else{
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    //What kind of quiz the user selected (Flag or Outline)
                    intent.putExtra("true",flag);
                    startActivity(intent);
                    noSelectedUserFlag=false;
                }
            }
        });

        //OnClick for the Outline quiz button
        mCountryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag=false;
                //If the user has no users selected
                if(!noSelectedUserFlag) {
                    showNoSelectedUserDialog();
                }
                else{
                    //Go to the quiz activity
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    //What kind of quiz the user selected (Flag or Outline)
                    intent.putExtra("false", flag);
                    startActivity(intent);
                    noSelectedUserFlag=false;
                }
            }
        });

        //OnClick for the Flag high score button
        mFlagScoreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                intent.putExtra("tittle","Flag High Score");
                intent.putExtra("isFlag",true);
                startActivity(intent);
            }
        });

        //OnClick for the Outline high score button
        mOutlineScoreIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HighScoreActivity.class);
                intent.putExtra("tittle","Outline High Score");
                intent.putExtra("isFlag",false);
                startActivity(intent);
            }
        });

        //OnClick for the user image view
        mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                startActivityForResult(intent, SELECTED_USER_REQ_CODE);
            }
        });
    }

    //Find the current selected user and store in Cursor
    private void getSelectedUserCursor(){
        if (dbManager != null && dbManager.openReadableDatabase()) {
            mCursor=dbManager.readSelectedUser();
        } else {
            mCursor=null;
        }
    }

    //If there is no user selected an alertDialog will inform the user that he has to select one so that his score will be saved
    private void showNoSelectedUserDialog() {
        getSelectedUserCursor();
        if(mCursor.getCount()==0){
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("No active user Selected");
            alertDialog.setMessage("If you continue your score will not be saved. Are you sure you want to continue?");

            //set the dialog OK action
            alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    noSelectedUserFlag =true;
                    dialog.dismiss();
                    if(flag){
                        mFlagBtn.performClick();
                    }
                    else{
                        mCountryBtn.performClick();
                    }
                }
            });

            //set the dialog CANCEL action
            alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    noSelectedUserFlag =false;
                    dialog.cancel();
                }
            });

            //show the dialog
            alertDialog.show();
        }
        else{
            noSelectedUserFlag =true;
            if(flag){
                mFlagBtn.performClick();
            }
            else{
                mCountryBtn.performClick();
            }
        }
    }
    //Animation for the user icon
    public void animateIt() {
        getSelectedUserCursor();
        //If the user hasn't selected a user the user icon will loop through green and red
        if (mCursor.getCount() == 0) {
            userIvAnimator = ObjectAnimator.ofInt(mUser, "colorFilter", Color.GREEN, Color.RED);
            userIvAnimator.setInterpolator(new LinearInterpolator());
            userIvAnimator.setDuration(1500);
            userIvAnimator.setRepeatCount(ValueAnimator.INFINITE);
            userIvAnimator.setRepeatMode(ValueAnimator.REVERSE);
        }
        //If the user has selected a user the icon will go from Yellow to White once.
        else{
            userIvAnimator = ObjectAnimator.ofInt(mUser, "colorFilter", Color.YELLOW, Color.WHITE);
            userIvAnimator.setInterpolator(new LinearInterpolator());
            userIvAnimator.setDuration(1000);

        }
        userIvAnimator.setEvaluator(new ArgbEvaluator());
        userIvAnimatorSet = new AnimatorSet();
        userIvAnimatorSet.play(userIvAnimator);
        userIvAnimatorSet.start();
    }
    //When it returns from the user activity check if a user is selected and animate the icon accordingly
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        try{
            userIvAnimatorSet.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (reqCode == SELECTED_USER_REQ_CODE) {
            animateIt();
        }
    }
}