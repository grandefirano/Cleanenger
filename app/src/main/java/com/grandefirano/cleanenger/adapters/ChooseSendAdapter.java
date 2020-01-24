package com.grandefirano.cleanenger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.single_items.UserData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseSendAdapter extends RecyclerView.Adapter<ChooseSendAdapter.ViewHolder> {


    private boolean isSelectedAll=false;
    private ArrayList<String> mList;

    private final OnItemListener mOnItemListener;
    private Context mContext;


    public ChooseSendAdapter(Context context, ArrayList<String> listOfFriends, OnItemListener onItemListener){
        mOnItemListener=onItemListener;
        mList=listOfFriends;
        mContext=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_person_send_item,parent,false);
        return new ViewHolder(v,mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        getUserInfoAndMakeItem(holder,mList.get(position));

        if(isSelectedAll){
            holder.mCheck.setImageResource(R.drawable.ic_check_box_purple);
        }else {
            holder.mCheck.setImageResource(R.drawable.ic_check_box_outline_blank_grey);
        }
    }

    @Override
    public int getItemCount() {
            return mList.size();
        }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mPersonTextView;
        private ImageView mCheck;

        private OnItemListener mOnItemListener;

        private ViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.personImageView);
            mPersonTextView=itemView.findViewById(R.id.nameOfPersonTextView);
            mCheck=itemView.findViewById(R.id.checkboxOfPerson);

            itemView.setOnClickListener(this);

            this.mOnItemListener=onItemListener;
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

        mGivenUserData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData=dataSnapshot.getValue(UserData.class);

                if(userData!=null) {
                    String username = userData.getUsername();
                    String profilePhoto = userData.getProfilePhoto();

                    holder.mPersonTextView.setText(username);
                    Picasso.with(mContext).load(profilePhoto)
                            .fit()
                            .centerCrop()
                            .into(holder.mImageView);
                }
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



