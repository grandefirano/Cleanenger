package com.grandefirano.cleanenger;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grandefirano.cleanenger.login.MainListAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FindPeopleAdapter extends RecyclerView.Adapter<FindPeopleAdapter.ViewHolder> {




        private ArrayList<SinglePersonSearchItem> mList;
        OnItemListener mOnItemListener;
        private Context mContext;

        public FindPeopleAdapter(Context context, ArrayList<SinglePersonSearchItem> listOfMessages, OnItemListener onItemListener){
            mOnItemListener=onItemListener;
            mList=listOfMessages;
            mContext=context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_search_item,parent,false);
            ViewHolder viewHolder=new ViewHolder(v,mOnItemListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            SinglePersonSearchItem currentItem=mList.get(position);

            Picasso.with(mContext).load(currentItem.getImageResource())
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);
            //DOWNLOAD PHOTO

            //holder.mImageView.setImageResource("");
            holder.mPersonTextView.setText(currentItem.getPersonText());



        }

        @Override
        public int getItemCount() {
            Log.d("ddd", String.valueOf(mList.size()));
            return mList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mImageView;
            public TextView mPersonTextView;


            OnItemListener mOnItemListener;


            public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
                super(itemView);
                mImageView=itemView.findViewById(R.id.personImageView);
                mPersonTextView=itemView.findViewById(R.id.nameOfPersonTextView);

                Log.d("ddd",mPersonTextView.getText().toString());

                this.mOnItemListener=onItemListener;

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mOnItemListener.onItemClick(getAdapterPosition());
            }
        }

        public interface OnItemListener{
            void onItemClick(int position);
        }


    }


