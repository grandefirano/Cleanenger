package com.grandefirano.cleanenger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.single_items.SinglePersonSearchItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FindPeopleAdapter extends RecyclerView.Adapter<FindPeopleAdapter.ViewHolder> implements Filterable {

    private boolean mIfOnlyFriends=false;
    private ArrayList<SinglePersonSearchItem> mList=new ArrayList<>();
    private ArrayList<SinglePersonSearchItem> mFullList;
    private ArrayList<SinglePersonSearchItem> mActualList= new ArrayList<>();

    private OnItemListener mOnItemListener;
    private OnRightButtonListener mOnRightButtonListener;
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

                //because returned value is always ArrayList
                @SuppressWarnings("unchecked")
                ArrayList<SinglePersonSearchItem> resultList=
                        (ArrayList<SinglePersonSearchItem>) results.values;

                mList.addAll(resultList);

                notifyDataSetChanged();
            }
        };

        public FindPeopleAdapter(Context context, ArrayList<SinglePersonSearchItem> listOfPeople, OnItemListener onItemListener,OnRightButtonListener onRightButtonListener){
            mOnItemListener=onItemListener;
            mOnRightButtonListener=onRightButtonListener;
            mFullList=listOfPeople;
            mContext=context;
        }

    private boolean isIfOnlyFriends() {
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
            View v= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_person_search_item,
                            parent,false);
            return new ViewHolder(v,mOnItemListener,mOnRightButtonListener);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            SinglePersonSearchItem currentItem=mList.get(position);
            showRightIcon(holder,currentItem.isFriend());

            Picasso.with(mContext).load(currentItem.getImageResource())
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView);

            holder.mPersonTextView.setText(currentItem.getPersonText());
        }

    private void showRightIcon(ViewHolder holder,boolean isFriend) {

            if(mIfOnlyFriends) {
                holder.mRightButtonImageView.setImageResource(R.drawable.ic_remove_gray);
                holder.mRightButtonImageView.setVisibility(View.VISIBLE);
            }else {
                if (isFriend) {
                    holder.mRightButtonImageView.setVisibility(View.INVISIBLE);
                } else {
                    holder.mRightButtonImageView.setImageResource(R.drawable.ic_person_add_black);
                    holder.mRightButtonImageView.setVisibility(View.VISIBLE);
                }
            }
    }

    @Override
        public int getItemCount() {
            return mList.size();
        }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ImageView mImageView;
            private TextView mPersonTextView;
            private ImageView mRightButtonImageView;

            OnItemListener mOnItemListener;
            OnRightButtonListener mOnRightButtonListener;


            private ViewHolder(@NonNull View itemView, OnItemListener onItemListener,OnRightButtonListener onRightButtonListener) {
                super(itemView);
                mImageView=itemView.findViewById(R.id.personImageView);
                mPersonTextView=itemView.findViewById(R.id.nameOfPersonTextView);
                mRightButtonImageView=itemView.findViewById(R.id.rightButtonImageView);


                mRightButtonImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mOnRightButtonListener.onRightButtonClick(getAdapterPosition());
                    }
                });
                this.mOnRightButtonListener=onRightButtonListener;
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

        public interface OnRightButtonListener{
            void onRightButtonClick(int position);
        }

        public void updateActualList(){
            showOnlyFriends(isIfOnlyFriends());
        }

    public ArrayList<SinglePersonSearchItem> getmList() {
        return mList;
    }
}



