package com.example.geographyquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    private TextView tittleTv;
    private TextView mUserNameTv;
    private TextView scoreTv;
    private ImageView flagItemIv;
    private ImageView mUserPictureIv;
    private SearchView searchSv;

    private RecyclerView countriesRv;
    private CountriesAdapter mCountriesAdapter;

    private UserDbManager dbManager;

    private MediaPlayer mMediaPlayer;

    private User user;
    private int currentScore=0;
    private int currentQuestion=0;
    private String scoreTittle;
    private boolean includeFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();

        //Find all views in activities
        tittleTv =  findViewById(R.id.tv_tittle);
        mUserNameTv =  findViewById(R.id.tv_user_name);
        scoreTv =  findViewById(R.id.tv_current_score);
        countriesRv =  findViewById(R.id.rv_high_score);
        searchSv = findViewById(R.id.sv_search);
        flagItemIv = findViewById(R.id.iv_question_image);
        mUserPictureIv = findViewById(R.id.iv_user_picture);

        //setup database manager
        dbManager = UserDbManager.getInstance(getApplicationContext());

        //start music method
        startMusic();
        //get selected user task
        new GetSelectedUserTask().execute();

        //initialize recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        countriesRv.setLayoutManager(layoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(countriesRv.getContext(),
                layoutManager.getOrientation());
        countriesRv.addItemDecoration(mDividerItemDecoration);

        ArrayList<Country> flagList;
        ArrayList<Country> countriesList;

        //If this is the flag quiz
        if(intent.hasExtra("true")){
            //get list with flags and countries
            flagList = CountryDataProvider.getCountriesFullList();
            countriesList = CountryDataProvider.getCountriesList();
            //Change tittle text view
            tittleTv.setText("Guess the Flag");
            scoreTittle="Flag High Score";
            includeFlag=false;
        }
        //if this is the country quiz
        else{
            //get list with flags and countries
            flagList = CountryDataProvider.getCountriesOutlineList();
            countriesList = CountryDataProvider.getCountriesFullList();
            //Change tittle text view
            tittleTv.setText("Guess the Country");
            scoreTittle="Outline High Score";
            includeFlag=true;
        }
        currentQuestion=FindQuestion(flagList);
        flagItemIv.setImageResource(flagList.get(currentQuestion).getFlag());
        SetupAdapter(countriesList,flagList,flagItemIv,scoreTittle);
        searchSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            //everytime the text changes in the search view get a new list of countries in the recycler view
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Country> searchedCountriesList = new ArrayList<Country>(countriesList);
                SetupAdapter(CountryDataProvider.getSearchedCountries(searchedCountriesList,newText),flagList,flagItemIv,scoreTittle);
                return false;
            }
        });

    }
    //when item in recycler view is clicked
    private void SetupAdapter(ArrayList<Country> countriesList,ArrayList<Country> flagList,ImageView flagItemIv,String tittle){
        mCountriesAdapter = new CountriesAdapter(includeFlag,this, countriesList,
                new CountriesAdapter.OnItemClickListener() {


                    @Override
                    public void onItemClick(Country item) {
                        //reset search view
                        searchSv.setQuery("", false);
                        OnClickAction(item,flagList,flagItemIv,tittle);
                    }
                });

        countriesRv.setAdapter(mCountriesAdapter);
    }
    //get a random image that wasn't the last question
    private int FindQuestion(ArrayList<Country> flagList){
        Random random = new Random();
        return random.nextInt(flagList.size());
    }
    //Action for when a country is pressed
    private void OnClickAction(Country item,ArrayList<Country> flagList,ImageView flagItemIv,String ScoreActivity){
        //if correct update the score and get a new question
        if(item.getName()==flagList.get(currentQuestion).getName()){
            currentScore++;
            scoreTv.setText(String.valueOf(currentScore));
            int previousQuestion = currentQuestion;
            currentQuestion=FindQuestion(flagList);
            while(previousQuestion ==currentQuestion){
                currentQuestion=FindQuestion(flagList);
            }
            flagItemIv.setImageResource(flagList.get(currentQuestion).getFlag());
        }
        //if incorrect go to Score activity
        else{
            Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
            intent.putExtra("score",String.valueOf(currentScore));
            intent.putExtra("tittle",String.valueOf(ScoreActivity));
            intent.putExtra("correctAnswer",flagList.get(currentQuestion).getName());
            startActivity(intent);
            finish();
        }
    }
    //Back icon press
    @Override
    public void onBackPressed() {
        showDeleteConfirmationDialog();
    }
    private void showDeleteConfirmationDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        //inform user that his score will not be saved
        alertDialog.setTitle("Return to Start Screen");
        alertDialog.setMessage("If you leave your score will not be saved. Are you sure you want to go back ?");

        //set the dialog OK action
        alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        });

        //set the dialog CANCEL action
        alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //show the dialog
        alertDialog.show();
    }

    //get selected user to put in image view and text view
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
    //Place image and username
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

    //Play background music while user in quiz activity
    private void startMusic(){
        float volume = 0.7f;
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.background_music);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(volume,volume);
        mMediaPlayer.start();
    }

    public void onPause() {
        super.onPause();
        mMediaPlayer.pause();
    }
    public void onResume() {
        super.onResume();
        mMediaPlayer.start();
    }

}
