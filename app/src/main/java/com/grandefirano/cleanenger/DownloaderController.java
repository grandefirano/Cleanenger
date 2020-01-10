package com.grandefirano.cleanenger;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.Activities.FindPeopleActivity;
import com.grandefirano.cleanenger.adapter.FindPeopleAdapter;
import com.grandefirano.cleanenger.messages.SinglePersonSearchItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DownloaderController {


    ImageButton mImageButton;
    EditText mSearchEditText;
    RecyclerView mRecyclerView;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    private FindPeopleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<SinglePersonSearchItem> listItems= new ArrayList<>();
    ArrayList<String> usernameList=new ArrayList<>();
    private Intent newIntent;
    private Context mContext;

//TODO:ISITIIMPORTAR?T?FG?SFG<D<G>?


    public DownloaderController(Context context) {
        mContext = context;
    }

    private void goToFindPeople(){
        Intent intent=new Intent(mContext,FindPeopleActivity.class);
        intent.putExtra("listItems",listItems);
        intent.putExtra("idList",idList);
        intent.putExtra("usernameList",usernameList);
        mContext.startActivity(intent);
    }

    public void downloadListForFindPeople(){
        listItems.clear();
        idList.clear();
        usernameList.clear();

        //BECAUSE ITS LAST
        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                goToFindPeople();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        mDatabase.child("users")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//

                        String username= (String) dataSnapshot.child("data").child("username").getValue();
                        String profilePhoto=(String)dataSnapshot.child("data").child("profile_photo").getValue();

                        listItems.add(new SinglePersonSearchItem(profilePhoto,username));

//                        listItems.add(new SingleMessageFeedItem(R.drawable.ic_android,
//                                String.valueOf(dataSnapshot.child("username").getValue()),
//                                String.valueOf(dataSnapshot.child("message").getValue()),
//                                (boolean)dataSnapshot.child("ifRead").getValue()));
                        idList.add(String.valueOf(dataSnapshot.getKey()));
                        usernameList.add(username);








                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }


}
