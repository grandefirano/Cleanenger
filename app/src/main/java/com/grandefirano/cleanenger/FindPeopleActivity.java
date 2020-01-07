package com.grandefirano.cleanenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.login.MainListAdapter;

import java.util.ArrayList;

public class FindPeopleActivity extends AppCompatActivity implements FindPeopleAdapter.OnItemListener {

    ImageButton mImageButton;
    EditText mSearchEditText;
    RecyclerView mRecyclerView;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<SinglePersonSearchItem> listItems= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        mImageButton=findViewById(R.id.searchPeopleImageButton);
        mSearchEditText=findViewById(R.id.searchPeopleEditText);
        mRecyclerView=findViewById(R.id.searchPeopleRecyclerView);

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);
        mAdapter= new FindPeopleAdapter(getApplicationContext(),listItems,this);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



        downloadListFromDatabase("");

    }

    public void searchPeople(View view){
        String searchName=mSearchEditText.getText().toString();

        downloadListFromDatabase("");


    }

    private void downloadListFromDatabase(String searchName){
        listItems.clear();
        idList.clear();



        mDatabase.child("users")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//

                        String username= (String) dataSnapshot.child("data").child("username").getValue();
                        String profilePhoto=(String)dataSnapshot.child("data").child("profile_photo").getValue();

                        listItems.add(new SinglePersonSearchItem(profilePhoto,username));
                        mAdapter.notifyDataSetChanged();

//                        listItems.add(new SingleMessageFeedItem(R.drawable.ic_android,
//                                String.valueOf(dataSnapshot.child("username").getValue()),
//                                String.valueOf(dataSnapshot.child("message").getValue()),
//                                (boolean)dataSnapshot.child("ifRead").getValue()));
                        idList.add(String.valueOf(dataSnapshot.getKey()));



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


    @Override
    public void onItemClick(int position) {

        Intent intent= new Intent(this, ChatActivity.class);
        intent.putExtra("id", idList.get(position));
        startActivity(intent);
    }
}
