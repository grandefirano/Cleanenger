/*
package com.grandefirano.cleanenger;


import android.graphics.Color;
import android.graphics.Typeface;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.adapter.MainListAdapter;
import com.grandefirano.cleanenger.singleItems.SingleMessageFeedItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class DownloaderController {

    public void downloadSingleMessageFeedItem(ArrayList<SingleMessageFeedItem> listOfSingleMessage){




    }

    private void getLastMessage(final MainListAdapter.ViewHolder holder, String chatId){

        DatabaseReference chatReference= FirebaseDatabase.getInstance().getReference()
                .child("chats").child(chatId).child("last_message");
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        final String myId=mAuth.getCurrentUser().getUid();




        chatReference
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String message=String.valueOf(dataSnapshot.child("message").getValue());
                        String messagePersonId=String.valueOf(dataSnapshot.child("uId").getValue());
                        boolean ifRead=(boolean)dataSnapshot.child("ifRead").getValue();
                        boolean ifMe=messagePersonId.equals(myId);

                        if(ifMe){
                            holder.mMessageTextView.setTextColor(Color.GRAY);
                            holder.mMessageTextView.setTypeface(holder.mMessageTextView.getTypeface(),
                                    Typeface.BOLD);
                            message="Me: "+message;
                            if(ifRead){
                                message="[Read] "+message;
                            }
                        }else{
                            if(ifRead){
                                holder.mMessageTextView.setTextColor(Color.GRAY);
                                holder.mMessageTextView.setTypeface(holder.mMessageTextView.getTypeface(),
                                        Typeface.NORMAL);

                            }else {
                                holder.mMessageTextView.setTextColor(Color.BLACK);
                                holder.mMessageTextView.setTypeface(holder.mMessageTextView.getTypeface(),
                                        Typeface.BOLD);
                            }
                        }


                        holder.mMessageTextView.setText(message);


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
    }

    private void getUserInfoAndMakeItem(final MainListAdapter.ViewHolder holder, final String userId, int position){
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


}
*/
