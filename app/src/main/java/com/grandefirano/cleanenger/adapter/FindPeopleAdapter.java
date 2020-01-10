package com.grandefirano.cleanenger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.singleItems.SinglePersonSearchItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

public class FindPeopleAdapter extends RecyclerView.Adapter<FindPeopleAdapter.ViewHolder> implements Filterable {




        private ArrayList<SinglePersonSearchItem> mList;
        private ArrayList<SinglePersonSearchItem> mListFull;
        OnItemListener mOnItemListener;
        OnAddButtonListener mOnAddButtonListener;
        private Context mContext;
        private Filter mFilter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<SinglePersonSearchItem> filteredList= new ArrayList<>();
                Log.d("ddddLISTFULL","ss");
                if(constraint==null || constraint.length()==0){
                    filteredList.addAll(mListFull);
                }else{
                    String filterPattern=constraint.toString().toLowerCase().trim();
                    Log.d("ddddLISTFULL","pelno");
                    Log.d("ddddLISTFULL",filterPattern);

                    for(SinglePersonSearchItem item: mListFull){
                        Log.d("ddddLISTFULL","ddd");
                        if(item.getPersonText().toLowerCase().contains(filterPattern)){
                            filteredList.add(item);
                            Log.d("ddddLISTFULL",item.getPersonText());
                        }
                    }
                }
                FilterResults results= new FilterResults();

                results.values=filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mList.clear();
                mList.addAll((ArrayList)results.values);
                notifyDataSetChanged();
            }
        };

        public void updateFullList(ArrayList<SinglePersonSearchItem> newList){
            mListFull=new ArrayList<>(newList);

        }


        public FindPeopleAdapter(Context context, ArrayList<SinglePersonSearchItem> listOfPeople, OnItemListener onItemListener,OnAddButtonListener onAddButtonListener){
            mOnItemListener=onItemListener;
            mOnAddButtonListener=onAddButtonListener;
            mList=listOfPeople;
            mContext=context;
            //to have two different
            for(SinglePersonSearchItem item:mList){
                Log.d("dddddd",item.getPersonText());
            }
            //TODO: ZROBIC TO TUTAJ
            mListFull=new ArrayList<>(mList);

            Log.d("dddddd","creating");




        }

        public void showOnlyFriends(boolean onlyFriends){
            if(onlyFriends){

            }else{

            }
        }




        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_search_item,parent,false);
            ViewHolder viewHolder=new ViewHolder(v,mOnItemListener,mOnAddButtonListener);
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

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mImageView;
            public TextView mPersonTextView;
            public AppCompatImageButton mAddButton;


            OnItemListener mOnItemListener;
            OnAddButtonListener mOnAddButtonListener;


            public ViewHolder(@NonNull View itemView, OnItemListener onItemListener,OnAddButtonListener onAddButtonListener) {
                super(itemView);
                mImageView=itemView.findViewById(R.id.personImageView);
                mPersonTextView=itemView.findViewById(R.id.nameOfPersonTextView);
                mAddButton=itemView.findViewById(R.id.addFriendButton);
                Log.d("ddd",mPersonTextView.getText().toString());
                mAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("dddd",String.valueOf(getAdapterPosition()));
                        mOnAddButtonListener.onAddClick(getAdapterPosition());
                    }
                });
                this.mOnAddButtonListener=onAddButtonListener;
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

        public interface OnAddButtonListener{
            void onAddClick(int position);
        }


    }


