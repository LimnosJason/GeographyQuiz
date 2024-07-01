package com.example.geographyquiz;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersRecyclerAdapter extends RecyclerView.Adapter<UsersRecyclerAdapter.ViewHolder>{
    public UsersRecyclerAdapter(OnItemClickListener listener, OnItemLongClickListener longListener) {
        this.listener = listener;
        this.longListener = longListener;
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(User user);
    }

    private LayoutInflater inflater;
    private Context context;
    private final OnItemClickListener listener;
    private final OnItemLongClickListener longListener;
    private Cursor mCursor;

    public UsersRecyclerAdapter(Context context, Cursor cursor, OnItemClickListener listener, OnItemLongClickListener longListener) {
        this.context = context;
        this.listener = listener;
        this.longListener = longListener;
        mCursor = cursor;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_user, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        User user = new User();
        user.setUserName(mCursor.getString(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_NAME)));
        user.setUserPicturePath(mCursor.getString(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_PICTURE)));
        user.setUserFlagScore(mCursor.getInt(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_FLAG_SCORE)));
        user.setUserOutlineScore(mCursor.getInt(mCursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_OUTLINE_SCORE)));
        user.setId(Long.parseLong(mCursor.getString(mCursor.getColumnIndexOrThrow(UserContract.UserEntry._ID))));
        holder.bind(user, listener, longListener);
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
        private CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.list_item_user_name_tv);
            imageView = itemView.findViewById(R.id.list_item_user_pic_iv);
        }

        public void bind(final User user, final OnItemClickListener listener, final OnItemLongClickListener longListener) {
            //Read textual data from the current cursor item and set to the views
            nameTv.setText(user.getUserName());

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(user);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    longListener.onItemLongClick(user);
                    return true;
                }
            });
        }
    }

}
