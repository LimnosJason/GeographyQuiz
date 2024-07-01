package com.example.geographyquiz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CountriesAdapter extends RecyclerView.Adapter<CountriesAdapter.ViewHolder>{



    public interface OnItemClickListener {
        void onItemClick(Country item);
    }
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<Country> mList;
    private final OnItemClickListener listener;
    private boolean includeFlag;
    public CountriesAdapter(boolean includeFlag,QuizActivity context, ArrayList<Country> countriesFullList, OnItemClickListener onItemClickListener) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.mList = countriesFullList;
        this.listener = onItemClickListener;
        this.includeFlag = includeFlag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(includeFlag){
            View view = inflater.inflate(R.layout.list_item_country_flag, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
        else{
            View view = inflater.inflate(R.layout.list_item_country, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTv;
        private ImageView flagItemIv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.tv_name_item);
            flagItemIv = itemView.findViewById(R.id.iv_flag_item);
        }

        public void bind(final Country item, final OnItemClickListener listener) {
            nameTv.setText(item.getName());
            if(includeFlag){
                flagItemIv.setImageResource(item.getFlag());
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
