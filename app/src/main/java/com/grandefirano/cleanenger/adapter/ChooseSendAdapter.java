package com.grandefirano.cleanenger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.singleItems.SinglePersonSearchItem;
import com.grandefirano.cleanenger.singleItems.UserData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseSendAdapter extends RecyclerView.Adapter<ChooseSendAdapter.ViewHolder> {


    private final OnItemListener mOnItemListener;
    private DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();

    private ArrayList<String> mList;
    private boolean[] mListOfChecked;



        private Context mContext;
//
//        public static final int SELECTION_ALL=0;
//        public static final int SELECTION_NOTHING=1;
//        public static final int SELECTION_CUSTOM=2;

    private boolean isSelectedAll=false;





        public ChooseSendAdapter(Context context, ArrayList<String> listOfFriends,boolean[] listOfChecked, OnItemListener onItemListener){
            mOnItemListener=onItemListener;
           mList=listOfFriends;
            mContext=context;
            mListOfChecked=listOfChecked;
        }




        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_send_item,parent,false);
            ViewHolder viewHolder=new ViewHolder(v,mOnItemListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("ddddddB","binding");
        getUserInfoAndMakeItem(holder,mList.get(position));


            if(isSelectedAll){
                holder.mCheck.setImageResource(R.drawable.ic_check_box_purple);
                Log.d("dddddddDDDDDDD","SELECT AL");
            }else {
                holder.mCheck.setImageResource(R.drawable.ic_check_box_outline_blank_grey);
                Log.d("dddddddDDDDDDD","SELECT ANOHING");
            }
            Log.d("dddddddDDDDDDD","CU");


        }

    @Override
        public int getItemCount() {
            return mList.size();
        }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView mImageView;
            public TextView mPersonTextView;
            public ImageView mCheck;


            OnItemListener mOnItemListener;


            public ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
                super(itemView);
                mImageView=itemView.findViewById(R.id.personImageView);
                mPersonTextView=itemView.findViewById(R.id.nameOfPersonTextView);
                mCheck=itemView.findViewById(R.id.checkboxOfPerson);
                //mCheck.setOnClickListener(this);

                this.mOnItemListener=onItemListener;

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                mOnItemListener.onItemClick(getAdapterPosition(),mCheck);



            }
        }

        public interface OnItemListener{
            void onItemClick(int position,ImageView imageView);

        }


    private void getUserInfoAndMakeItem( final ChooseSendAdapter.ViewHolder holder, final String userId){
        DatabaseReference mGivenUserData= FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("data");



        mGivenUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData=dataSnapshot.getValue(UserData.class);

                String username=userData.getUsername();
                String profilePhoto=userData.getProfilePhoto();

                holder.mPersonTextView.setText(username);
                Picasso.with(mContext).load(profilePhoto)
                        .fit()
                        .centerCrop()
                        .into(holder.mImageView);



            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }


    public void setSelectedAll(boolean selectedAll) {
        isSelectedAll = selectedAll;
    }

    public boolean getSelectedAll() {
        return isSelectedAll;
    }
}



