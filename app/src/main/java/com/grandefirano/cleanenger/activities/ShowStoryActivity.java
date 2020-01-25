package com.grandefirano.cleanenger.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grandefirano.cleanenger.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowStoryActivity extends AppCompatActivity {


    ImageView mStoryImageView;

    ArrayList<String> mStoryPhotos= new ArrayList<>();
    ArrayList<String> mStoryIds= new ArrayList<>();

    String mStoryUserId;

    String myId;
    private int position;
    DatabaseReference mSnapsReference;
    DatabaseReference mUserSnapsReference;


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

        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if(user==null){
            finish();
        }else {
            myId = user.getUid();
        }
        mUserSnapsReference= FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(myId).child("snaps");
        mSnapsReference=FirebaseDatabase.getInstance().getReference()
                .child("snaps");

        mStoryImageView= findViewById(R.id.showStoryImageView);


        mStoryUserId=getIntent().getStringExtra("id");
        View mNext=findViewById(R.id.gotoNextView);

        mNext.setOnClickListener(mOnNextClickListener);

        getStoriesFromUser(mStoryUserId);


    }


    private void getStoriesFromUser(String id){
        mStoryIds.clear();
        mStoryPhotos.clear();
        mUserSnapsReference.child(id)
            .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    mStoryIds.add(snapshot.getKey());
                    mStoryPhotos.add(String.valueOf(snapshot.getValue()));
                }
                showNext();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void showNext(){
        if(mStoryPhotos.size()>position){
        //where is in bound
        Picasso.with(this).load(mStoryPhotos.get(position))
                .fit()
                .centerInside()
                .into(mStoryImageView);

        deleteSnap(position);
        position++;

        }else{
            //when is out of bound
            finish();
        }



    }

    private void deleteSnap(int pos) {

        final String snapId=mStoryIds.get(pos);

        //DELETE SNAP FROM DATABASE

        mUserSnapsReference.child(mStoryUserId).child(snapId).removeValue();

        mSnapsReference.child(snapId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {

                    mSnapsReference.child(snapId).child(myId).removeValue();

                    if (dataSnapshot.getChildrenCount() == 0) {
                        //DELETE SNAP FROM STORAGE
                        StorageReference snapToDeleteRef = FirebaseStorage.getInstance()
                                .getReference().child("snaps").child(mStoryUserId).child(snapId + ".jpg");
                        snapToDeleteRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("LOG", "Photo was deleted");
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


    }


}
