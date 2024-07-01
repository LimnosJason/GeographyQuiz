package com.example.geographyquiz;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class HighScoreRecyclerAdapter extends RecyclerView.Adapter<HighScoreRecyclerAdapter.ViewHolder>{

    private LayoutInflater inflater;
    private Context context;
    private Cursor mCursor;
    private int cursorPosition;
    private boolean isFlag;
    public HighScoreRecyclerAdapter(Context context, Cursor cursor,boolean isFlag) {
        this.context = context;
        this.isFlag = isFlag;
        mCursor = cursor;
        inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public HighScoreRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_high_score, parent, false);
        HighScoreRecyclerAdapter.ViewHolder holder = new HighScoreRecyclerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HighScoreRecyclerAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        cursorPosition=mCursor.getPosition();
        User user = new User();
        user.setUserName(mCursor.getString(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME)));
        user.setUserPicturePath(mCursor.getString(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PICTURE)));
        user.setUserFlagScore(mCursor.getInt(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_FLAG_SCORE)));
        user.setUserOutlineScore(mCursor.getInt(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_OUTLINE_SCORE)));
        user.setId(Long.parseLong(mCursor.getString(mCursor.getColumnIndexOrThrow(UserContract.UserEntry._ID))));
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return(0);
        }

        return(mCursor.getCount());
    }

    void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private TextView positionTv;
        private TextView highScoreTv;
        private CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.list_item_user_name_tv);
            positionTv = itemView.findViewById(R.id.list_item_position_tv);
            highScoreTv = itemView.findViewById(R.id.list_item_user_score_tv);
            imageView = itemView.findViewById(R.id.list_item_user_pic_iv);
        }

        public void bind(final User user) {
            nameTv.setText(user.getUserName());
            positionTv.setText(String.valueOf(cursorPosition+1));
            //Place gold ,silver ,bronze colors for 1st 2nd and 3rd place
            if(cursorPosition+1==1){positionTv.setTextColor(Color.parseColor("#f8c13b"));}
            if(cursorPosition+1==2){positionTv.setTextColor(Color.parseColor("#c0c0c0"));}
            if(cursorPosition+1==3){positionTv.setTextColor(Color.parseColor("#88562e"));}
            if(isFlag){
                highScoreTv.setText(String.valueOf(user.getUserFlagScore()));
            }
            else{
                highScoreTv.setText(String.valueOf(user.getUserOutlineScore()));
            }

            //Read the path
            String imagePath = user.getUserPicturePath();

            //Use Picasso library to set the image from the file path to the Image View
            if (imagePath != null && !imagePath.equals("")) {
                Picasso.get()
                        .load(new File(imagePath))
                        .fit().centerInside()
                        .placeholder(R.drawable.img_contact_logo)
                        .error(R.drawable.img_contact_logo)
                        .into(imageView);
            } else {//if no path, use the default image from the app resources
                Picasso.get()
                        .load(R.drawable.img_contact_logo)
                        .fit().centerInside()
                        .into(imageView);
            }
        }
    }
}
