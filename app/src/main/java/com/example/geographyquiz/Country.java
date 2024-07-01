package com.example.geographyquiz;

public class Country {

    private String mName;
    private int mFlag;

    public Country(){
    }
    public Country(String mName){
        this.mName = mName;
    }

    public Country(String mName, int flag) {
        this.mName = mName;
        this.mFlag = flag;
    }

    public String getName() {
        return mName;
    }

    public int getFlag() {
        return mFlag;
    }
}
