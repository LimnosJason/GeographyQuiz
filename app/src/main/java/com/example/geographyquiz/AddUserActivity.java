package com.example.geographyquiz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUserActivity extends AppCompatActivity {
    private EditText mUserNameEt;
    private ImageView mCameraIv;
    private ImageView mGalleryIv;
    private CircleImageView mUserPictureIv;
    private Button mCancelBtn;
    private Button mSaveBtn;

    private Uri selectedImageUri = null;

    private UserDbManager dbManager;

    private static final int IMAGE_REQ_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        //Find all views in activities
        mUserNameEt=findViewById(R.id.et_user_name);
        mCameraIv = findViewById(R.id.iv_camera);
        mGalleryIv = findViewById(R.id.iv_gallery);
        mUserPictureIv = findViewById(R.id.iv_user_picture);
        mCancelBtn = findViewById(R.id.btn_cancel);
        mSaveBtn = findViewById(R.id.btn_save);

        //setup database manager
        dbManager = UserDbManager.getInstance(getApplicationContext());

        //Action when the user presses the camera image view
        mCameraIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissionCamera()&&checkAndRequestPermissionStorage()){
                    takePicture();
                }
            }
        });
        //Action when the user presses the gallery image view
        mGalleryIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImageFromGallery();
            }
        });
        //finish activity
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //save new user in the db
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUserNameEt.getText().toString().trim().length() > 10){
                    Snackbar.make(mSaveBtn, "UserName cant be over 10 characters", Snackbar.LENGTH_SHORT).show();
                }
                else if(mUserNameEt.getText().toString().trim().length() <= 0){
                    Snackbar.make(mSaveBtn, "UserName cant be empty", Snackbar.LENGTH_SHORT).show();
                }
                else{
                    storeUser();
                }
            }
        });
    }
    //take picture method
    private void takePicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager()) != null){
            this.startActivityIntentCamera.launch(takePicture);
        }
    }
    //take picture and store uri in database
    ActivityResultLauncher<Intent> startActivityIntentCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        if (data != null) {
                            //when picture is taken
                            Bundle bundle = data.getExtras();

                            Bitmap bitmapImage = (Bitmap) bundle.get("data");
                            //change bitmap Image to uri to store in database and place to user Image view
                            Uri tempUri = getImageUri(getApplicationContext(), bitmapImage);
                            mUserPictureIv.setImageBitmap(bitmapImage);
                            selectedImageUri = tempUri;
                        }
                    }
                }
            });

    //get uri from bitmap image
    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    //Ask player for permission to use the camera
    private boolean checkAndRequestPermissionCamera(){
        if(Build.VERSION.SDK_INT>=23){
            int cameraPermission = ActivityCompat.checkSelfPermission( AddUserActivity.this, Manifest.permission.CAMERA);
            if(cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(AddUserActivity.this, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }
    //Ask player for permission to store images
    private boolean checkAndRequestPermissionStorage() {


        if(Build.VERSION.SDK_INT>=23){
            int cameraPermission = ActivityCompat.checkSelfPermission( AddUserActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(AddUserActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 20);
                return false;
            }
        }
        return true;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            takePicture();
            Toast.makeText(AddUserActivity.this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(AddUserActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
        }
    }
    //Select image from gallery
    private void selectImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        //send the intent using startActivityForResult with the image request code
        //the expected result will be an intent containing the selected image
        startActivityForResult(Intent.createChooser(intent, "Select user photo"), IMAGE_REQ_CODE);
    }
    //Store user in database
    private void storeUser() {
        User user = new User(
                mUserNameEt.getText().toString(),
                0,0,
                null);

        new InsertUserTask().execute(user);

    }

    //Task to insert user in the database and finish current activity
    private class InsertUserTask extends AsyncTask<User, Void, Long> {

        User user;

        @Override
        protected Long doInBackground(User... users) {

            //get the user object to insert
            user = users[0];

            //Check if there is also an image selected from the gallery
            if (selectedImageUri != null) {

                //if yes, then copy the image to the local application files and set the path to the user object
                user.setUserPicturePath(ImageUtils.copyImage(selectedImageUri, getApplicationContext()));
            }

            //check if there is a Db manager initialized and open a writable database
            if (dbManager != null && dbManager.openWritableDatabase()) {
                //Create the new user on the Database using createUser from the DbManager and return the result (id)
                return dbManager.createUser(user);
            } else { //else return 0
                return 0L;
            }
        }


        @Override
        protected void onPostExecute(Long insertedRowId) {
            //check the insertion result
            if (insertedRowId > 0) {//the insertion succeeded
                //inform the UI
                Snackbar.make(mSaveBtn, "User has been added", Snackbar.LENGTH_SHORT).show();

                //update the activity Result (to be used from the calling activity i.e. the MainActivity)
                AddUserActivity.this.setResult(RESULT_OK);

                //change the activity mode to EDIT
                user.setId(insertedRowId); //set the id of the newly created row
                finish();
            }
            // release database connection resources
            dbManager.closeDatabase();

        }
    }
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        // check if the action was successful
        if (resCode == RESULT_OK) {
            //check if the request code is the image request
            if (reqCode == IMAGE_REQ_CODE) {
                //get the data from the result (actually a uri to the selected image) and put it in the selectedImageUri variable
                selectedImageUri = data.getData();
                // check the uri and if it is not null set the image to the image view
                if (selectedImageUri != null) {
                    mUserPictureIv.setImageURI(selectedImageUri);
                }
            }
        }
    }
}
