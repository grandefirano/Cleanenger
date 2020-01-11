package com.grandefirano.cleanenger.adapter;


import android.content.Context;
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
import com.grandefirano.cleanenger.UserData;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{


    private Context mContext;
    private List<String> mStoriesList;

    public StoryAdapter(Context context, List<String> storiesList) {
        mContext = context;
        mStoriesList = storiesList;
        Log.d("ddddstory", String.valueOf(mStoriesList.size()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view= LayoutInflater.from(mContext).inflate(R.layout.single_main_screen_story_item,parent,false);


        return new StoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String mId=mStoriesList.get(position);
        getUserInfo(holder,mId,position);

        Log.d("dddd","ekiiii");
        //add lsitener

    }

    @Override
    public int getItemCount() {
        return mStoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView storyImageView;
        public TextView storyNameTextView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            storyImageView=itemView.findViewById(R.id.StoryImageView);
            storyNameTextView=itemView.findViewById(R.id.storyTextView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    private void getUserInfo(final ViewHolder holder, String userId, int position){
        DatabaseReference mGivenUserData= FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("data");
        mGivenUserData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData=dataSnapshot.getValue(UserData.class);

                Picasso.with(mContext).load(userData.getProfilePhoto())
                        .fit()
                        .centerCrop()
                        .into(holder.storyImageView);
                holder.storyNameTextView.setText(userData.getUsername());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

}
