package com.example.geographyquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {
    private TextView mUserNameTv;
    private TextView mFlagScoreTv;
    private TextView mOutlineScoreTv;
    private CircleImageView mUserPictureIv;
    private ImageView backArrowIv;
    private FloatingActionButton mFloatingBtn;

    private UsersRecyclerAdapter mRecyclerAdapter;
    private RecyclerView mUsersRecyclerView;

    private User lastUser;

    private Cursor mCursor;
    private UserDbManager dbManager;

    private static final int ADD_USER_REQ_CODE = 245;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //Find all views in activities
        mUserNameTv = findViewById(R.id.tv_user_name);
        mFlagScoreTv = findViewById(R.id.tv_flag_score);
        mOutlineScoreTv = findViewById(R.id.tv_outline_score);
        mUserPictureIv = findViewById(R.id.iv_user_picture);
        backArrowIv = findViewById(R.id.iv_back_arrow);
        mFloatingBtn = findViewById(R.id.fab);
        mUsersRecyclerView = findViewById(R.id.rv_users);

        //setup database manager
        dbManager = UserDbManager.getInstance(getApplicationContext());

        populateList();
        new GetSelectedUserTask().execute();

        //add new user button
        mFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewUser();
            }
        });

        //return to main activity
        backArrowIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeActivity();
            }
        });
    }

    //setup user recycler view
    private void populateList() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mUsersRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mUsersRecyclerView.getContext(),
                layoutManager.getOrientation());
        mUsersRecyclerView.addItemDecoration(mDividerItemDecoration);
        mUsersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerAdapter = new UsersRecyclerAdapter(this,
                mCursor,
                new UsersRecyclerAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(User user) {
                        selectUser(user);
                    }
                },
                new UsersRecyclerAdapter.OnItemLongClickListener() {
                    @Override
                    public void onItemLongClick(User user) {
                        //check if the cursor associated with the adapter can move to the specific position
                        if (null!= user) {
                            //display the dialog to confirm deletion
                            showDeleteConfirmationDialog(user);
                        }
                    }
                });
        mUsersRecyclerView.setAdapter(mRecyclerAdapter);

        new GetUsersTask().execute();
    }

    //select a new user when clicked
    private void selectUser(User user){
        //change name, flag score, outline score, and user photo
        mUserNameTv.setText(user.getUserName());
        mFlagScoreTv.setText(String.valueOf(user.getUserFlagScore()));
        mOutlineScoreTv.setText(String.valueOf(user.getUserOutlineScore()));

        if(lastUser!=null){
            lastUser.setSelected(false);
            updateSelected(lastUser);
        }
        lastUser=user;
        user.setSelected(true);
        updateSelected(user);

        String imagePath = user.getUserPicturePath();

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
    }

    private void addNewUser() {
        Intent intent = new Intent(UserActivity.this, AddUserActivity.class);
        startActivityForResult(intent, ADD_USER_REQ_CODE);
    }

    //when long press on item ask player if he wants to delede user
    private void showDeleteConfirmationDialog(User user) {
        //create and initialise an alert dialog
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete entry");
        alertDialog.setMessage("Are you sure you want to delete the selected user?");

        //set the dialog OK action
        alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                resetViews();
                //Delete the user from the database (asynchronously)
                new DeleteUserTask(user).execute();
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

    //Get current selected user
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
            if(cursor!=null){
                try {
                    //Show last selected user
                    cursor.moveToFirst();
                    populateUser(cursor);
                    mUserNameTv.setText(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME)));
                    mFlagScoreTv.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_FLAG_SCORE))));
                    mOutlineScoreTv.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_OUTLINE_SCORE))));

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

    //input last selected user information
    private void populateUser(Cursor cursor){
        lastUser = new User();
        lastUser.setUserName(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME)));
        lastUser.setUserPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PICTURE)));
        lastUser.setUserFlagScore(cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_FLAG_SCORE)));
        lastUser.setUserOutlineScore(cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_OUTLINE_SCORE)));
        lastUser.setId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry._ID))));
    }

    private class GetUsersTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            // check if dbManager is initialized and get a readable database
            if (dbManager != null && dbManager.openReadableDatabase()) {
                //get all the users and return the resulted cursor
                return dbManager.readUsers();
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
                //update the adapter's with the new cursor (this also updates the List)
                mRecyclerAdapter.setCursor(cursor);
                //release the database resources
            }
        }
    }

    private class DeleteUserTask extends AsyncTask<Void, Void, Cursor> {

        long id;
        String imagePath;
        boolean deleteImageIndicator = false;
        User mUser;

        public DeleteUserTask(User user) {
            super();
            mUser = user;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            //get the id of the item user) to delete from the cursor
            id = mUser.getId();

            //get the the path of the associated image
            imagePath = mUser.getUserPicturePath();

            // check if dbManager is initialized and get a writable database
            if (dbManager != null && dbManager.openWritableDatabase()) {
                //delete the user with the selected id and if it is successful
                if (dbManager.deleteUser(id)) {

                    //re-query the db and return the updated result
                    return dbManager.readUsers();
                } else {
                    return null;
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

                //update the adapter's with the new cursor (this also updates the List)
                mRecyclerAdapter.setCursor(cursor);

                //release the database resources
                //dbManager.closeDatabase();

                //inform the UI
                if (deleteImageIndicator)
                    Snackbar.make(mUsersRecyclerView, "User #" + id + " and all associated files have been removed",
                            Snackbar.LENGTH_SHORT).show();
                else
                    Snackbar.make(mUsersRecyclerView, "User has been removed",
                            Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void resetViews(){
        mUserNameTv.setText("User Name");
        mFlagScoreTv.setText("0");
        mOutlineScoreTv.setText("0");
        mUserPictureIv.setImageResource(R.drawable.img_contact_logo);
    }
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        //check if the requested action is EDIT or INSERT
        if (reqCode == ADD_USER_REQ_CODE) {
                new GetUsersTask().execute();
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }
    private void closeActivity(){
        finish();
    }
    private void updateSelected(User user){
        if (dbManager != null && dbManager.openWritableDatabase()) {
            dbManager.updateUser(user);
        }
    }

}
