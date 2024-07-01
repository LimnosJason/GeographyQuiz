package com.example.geographyquiz;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String mUserName;
    private int mUserFlagScore;
    private int mUserOutlineScore;
    private String mUserPicturePath;
    private boolean mSelected;
    private long mId;

    public User(){
    }

    protected User(Parcel in) {
    }

    public User(String mUserName, int mUserFlagScore, int mUserOutlineScore, String mUserPicturePath, long mId) {
        this.mUserName = mUserName;
        this.mUserFlagScore = mUserFlagScore;
        this.mUserOutlineScore = mUserOutlineScore;
        this.mUserPicturePath = mUserPicturePath;
        this.mSelected = false;
        this.mId = mId;
    }

    public User(String mUserName, int mUserFlagScore, int mUserOutlineScore, String mUserPicturePath) {
        this.mUserName = mUserName;
        this.mUserFlagScore = mUserFlagScore;
        this.mUserOutlineScore = mUserOutlineScore;
        this.mUserPicturePath = mUserPicturePath;
        this.mSelected = false;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public int getUserFlagScore() {
        return mUserFlagScore;
    }

    public void setUserFlagScore(int mUserFlagScore) {
        this.mUserFlagScore = mUserFlagScore;
    }

    public int getUserOutlineScore() {
        return mUserOutlineScore;
    }

    public void setUserOutlineScore(int mUserOutlineScore) {
        this.mUserOutlineScore = mUserOutlineScore;
    }

    public String getUserPicturePath() {
        return mUserPicturePath;
    }

    public void setUserPicturePath(String mUserPicturePath) {
        this.mUserPicturePath = mUserPicturePath;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUserName);
        dest.writeInt(this.mUserFlagScore);
        dest.writeInt(this.mUserOutlineScore);
        dest.writeString(this.mUserPicturePath);
        dest.writeLong(this.mId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(this.mSelected);
        }
    }


    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

}
