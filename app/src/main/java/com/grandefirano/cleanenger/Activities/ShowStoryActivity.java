package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ShowStoryActivity extends AppCompatActivity {


    ImageView mStoryImageView;


    ArrayList<String> mStoryPhotos= new ArrayList<>();
    ArrayList<String> mStoryIds= new ArrayList<>();

    String mStoryUserId;

    String myId;
    private int position;

    DatabaseReference mSnapsReference;
    FirebaseAuth mAuth;

    private View.OnClickListener mOnNextClickListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            showNext();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_story);

        position=0;

        mAuth=FirebaseAuth.getInstance();
        myId=mAuth.getCurrentUser().getUid();
        mSnapsReference= FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(myId).child("snaps");

        mStoryImageView= findViewById(R.id.showStoryImageView);



        mStoryUserId=getIntent().getStringExtra("id");
        View mNext=findViewById(R.id.gotoNextView);

        mNext.setOnClickListener(mOnNextClickListener);

        getStoriesFromUser(mStoryUserId);


    }


    private void getStoriesFromUser(String id){
        mStoryIds.clear();
        mStoryPhotos.clear();
        mSnapsReference.child(id)
            .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mStoryIds.add(snapshot.getKey());
                    mStoryPhotos.add(String.valueOf(snapshot.getValue()));
                    Log.d("ddddY",String.valueOf(snapshot.getValue()));
                    showNext();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showNext(){

        if(mStoryPhotos.size()>position){
            //when is in bound
        Picasso.with(this).load(mStoryPhotos.get(0))
                .fit()
                .centerCrop()
                .into(mStoryImageView);

        position++;
        }else{
            //when is out of bound
            finish();
        }

    }


}
