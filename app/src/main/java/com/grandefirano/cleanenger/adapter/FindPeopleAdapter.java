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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.singleItems.SinglePersonSearchItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

public class FindPeopleAdapter extends RecyclerView.Adapter<FindPeopleAdapter.ViewHolder> implements Filterable {



        private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        private boolean mIfOnlyFriends=false;
    private ArrayList<SinglePersonSearchItem> mList=new ArrayList<>();
        private ArrayList<SinglePersonSearchItem> mFullList;
        private ArrayList<SinglePersonSearchItem> mActualList= new ArrayList<>();

        OnItemListener mOnItemListener;
        OnAddButtonListener mOnAddButtonListener;
        private Context mContext;
        private Filter mFilter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<SinglePersonSearchItem> filteredList= new ArrayList<>();


                if(constraint==null || constraint.length()==0){
                    filteredList.addAll(mActualList);
                }else{
                    String filterPattern=constraint.toString().toLowerCase().trim();


                    for(SinglePersonSearchItem item: mActualList){

                        if(item.getPersonText().toLowerCase().contains(filterPattern)){
                            filteredList.add(item);

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
                Log.d("dddddddMLTIS", String.valueOf(mList.size()));
                Log.d("dddddddMLTIS", String.valueOf(mList.size()));
            }
        };




        public FindPeopleAdapter(Context context, ArrayList<SinglePersonSearchItem> listOfPeople, OnItemListener onItemListener,OnAddButtonListener onAddButtonListener){
            mOnItemListener=onItemListener;
            mOnAddButtonListener=onAddButtonListener;
            mFullList=listOfPeople;
            mContext=context;

            //to have two different




        }

    public boolean isIfOnlyFriends() {
        return mIfOnlyFriends;
    }

    public void showOnlyFriends(boolean onlyFriends){
            mIfOnlyFriends=onlyFriends;



            if(onlyFriends){

                mList.clear();
                mActualList.clear();

                for(SinglePersonSearchItem item:mFullList) {
                    if (item.isFriend())
                        mActualList.add(item);

                }


                mList.addAll(mActualList);

            }else{
                mList.clear();
                mList.addAll(mFullList);
                mActualList=new ArrayList<>(mFullList);


            }

            notifyDataSetChanged();
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
            showRightIcon(holder,currentItem.isFriend());

            Picasso.with(mContext).load(currentItem.getImageResource())
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);
            //DOWNLOAD PHOTO

            //holder.mImageView.setImageResource("");
            holder.mPersonTextView.setText(currentItem.getPersonText());



        }

    private void showRightIcon(ViewHolder holder,boolean isFriend) {
            if(mIfOnlyFriends) {
                holder.mAddFriendImageView.setImageResource(R.drawable.ic_remove_gray);
                holder.mAddFriendImageView.setVisibility(View.VISIBLE);
            }else {
                if (isFriend) {
                    holder.mAddFriendImageView.setVisibility(View.INVISIBLE);
                } else {
                    holder.mAddFriendImageView.setImageResource(R.drawable.ic_person_add_black);
                    holder.mAddFriendImageView.setVisibility(View.VISIBLE);
                }
            }

    }

    @Override
        public int getItemCount() {
            Log.d("ddddddddddListSize", String.valueOf(mList.size()));
            return mList.size();
        }

    @Override
    public Filter getFilter() {
        return mFilter;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mImageView;
            public TextView mPersonTextView;
            public ImageView mAddFriendImageView;


            OnItemListener mOnItemListener;
            OnAddButtonListener mOnAddButtonListener;


            public ViewHolder(@NonNull View itemView, OnItemListener onItemListener,OnAddButtonListener onAddButtonListener) {
                super(itemView);
                mImageView=itemView.findViewById(R.id.personImageView);
                mPersonTextView=itemView.findViewById(R.id.nameOfPersonTextView);
                mAddFriendImageView=itemView.findViewById(R.id.addFriendImageView);

                mAddFriendImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



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

        public void updateActualList(){
            showOnlyFriends(isIfOnlyFriends());
        }

        //init



    public ArrayList<SinglePersonSearchItem> getmList() {
        return mList;
    }
}



