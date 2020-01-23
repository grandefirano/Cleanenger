package com.grandefirano.cleanenger.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
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
import com.grandefirano.cleanenger.single_items.LastMessage;
import com.grandefirano.cleanenger.single_items.UserData;
import com.grandefirano.cleanenger.Utilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ViewHolder> {


    private ArrayList<String> mChatIdList;
    private ArrayList<String> mUserIdList;
    private OnItemListener mOnItemListener;
    private Context mContext;
    private String myId;

    public MainListAdapter(Context context,String myId,OnItemListener onItemListener,ArrayList<String> chatIdList,ArrayList<String> userIdList){
        mOnItemListener=onItemListener;
        mUserIdList=userIdList;
        mChatIdList=chatIdList;
        mContext=context;
        this.myId=myId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message_feed_item,parent,false);
        return new ViewHolder(v,mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String mChatId=mChatIdList.get(position);
        String mUserId=mUserIdList.get(position);

        makeAnItem(holder,mUserId,mChatId);
    }

    @Override
    public int getItemCount() {

        return mUserIdList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mPersonTextView;
        private TextView mMessageTextView;
        private TextView mDateTextView;

        OnItemListener mOnItemListener;


        private ViewHolder(@NonNull View itemView,OnItemListener onItemListener) {
            super(itemView);
            mImageView=itemView.findViewById(R.id.personImageView);
            mPersonTextView=itemView.findViewById(R.id.nameOfPersonTextView);
            mMessageTextView=itemView.findViewById(R.id.messageOfPersonTextView);
            mDateTextView=itemView.findViewById(R.id.messageDateTextView);

            this.mOnItemListener=onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mOnItemListener.onItemClick(getAdapterPosition());
        }
    }

    private void makeAnItem(MainListAdapter.ViewHolder holder,String userId,String chatId){
        getUserInfoAndMakeItem(holder,userId);
        getLastMessage(holder,chatId);
    }

    private void getLastMessage(final MainListAdapter.ViewHolder holder,String chatId){

        DatabaseReference lastMessageReference=FirebaseDatabase.getInstance().getReference()
                .child("chats").child(chatId).child("last_message");

        lastMessageReference
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LastMessage lastMessage=dataSnapshot.getValue(LastMessage.class);

                if(lastMessage!=null) {
                    String message = lastMessage.getMessage();
                    String messagePersonId = lastMessage.getuId();
                    boolean ifRead = lastMessage.isIfRead();

                    long date = lastMessage.getDate();
                    String dateString = Utilities.getProperDateFormat(date, false);

                    boolean ifMe = messagePersonId.equals(myId);

                    if (ifMe) {

                        holder.mMessageTextView.setTextColor(Color.GRAY);
                        holder.mMessageTextView.setTypeface(holder.mMessageTextView.getTypeface(),
                                Typeface.NORMAL);
                        message = "Me: " + message;
                        if (ifRead) {
                            holder.mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_black, 0, 0, 0);
                        }
                    } else {
                        if (ifRead) {
                            holder.mMessageTextView.setTextColor(Color.GRAY);

                            holder.mMessageTextView.setTypeface(holder.mMessageTextView.getTypeface(),
                                    Typeface.NORMAL);

                        } else {

                            holder.mMessageTextView.setTextColor(Color.BLACK);
                            holder.mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            holder.mMessageTextView.setTypeface(holder.mMessageTextView.getTypeface(),
                                    Typeface.BOLD);
                        }
                    }
                    holder.mDateTextView.setText(dateString);
                    holder.mMessageTextView.setText(message);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getUserInfoAndMakeItem(final MainListAdapter.ViewHolder holder, final String userId){
        DatabaseReference mGivenUserData= FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("data");
        mGivenUserData.addValueEventListener(new ValueEventListener() {
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

    public interface OnItemListener{
        void onItemClick(int position);
    }


}
