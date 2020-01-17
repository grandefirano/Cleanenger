package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.adapter.FindPeopleAdapter;
import com.grandefirano.cleanenger.singleItems.SinglePersonSearchItem;
import com.grandefirano.cleanenger.singleItems.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class FindPeopleActivity extends AppCompatActivity implements FindPeopleAdapter.OnItemListener, FindPeopleAdapter.OnAddButtonListener {


    RecyclerView mRecyclerView;
    Switch mSwitch;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mChatClickedDatabaseReference;

    private FindPeopleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String myId;

    ArrayList<String> mFriendsIdList= new ArrayList<>();
    ArrayList<SinglePersonSearchItem> mTemporaryList=new ArrayList<>();


    private Intent newIntent;
    private Context mContext;


    ValueEventListener mOnChatIdValueListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String mChatId;

            if(!dataSnapshot.exists()){
                //If not exists
                mChatId= UUID.randomUUID().toString();
            }else{
                //If exists
                mChatId=dataSnapshot.getValue().toString();
            }
            newIntent.putExtra("chatId",mChatId);
            startActivity(newIntent);

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            //TODO:
            if(isChecked){
                mAdapter.showOnlyFriends(true);
            }
            else{
                mAdapter.showOnlyFriends(false);
            }

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem=menu.findItem(R.id.action_search);
        SearchView mSearchView= (SearchView) searchItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);

                return false;
            }
        });
        return true;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);


        mRecyclerView=findViewById(R.id.searchPeopleRecyclerView);
        mSwitch=findViewById(R.id.switchIfFriends);

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mContext=getApplicationContext();

        myId=mAuth.getCurrentUser().getUid();


        mSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);
        mAdapter= new FindPeopleAdapter(mContext,mTemporaryList,FindPeopleActivity.this,FindPeopleActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        downloadListFromDatabase();




    }



    private void downloadListFromDatabase(){

        mTemporaryList.clear();


        //Add friends
        mDatabase.child("users").child(myId).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final String friendId=dataSnapshot.getKey();
                mFriendsIdList.add(friendId);

            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                final String friendId=dataSnapshot.getKey();
                mFriendsIdList.remove(friendId);


            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        //ADD ALL
        mDatabase.child("users")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        UserData userData=dataSnapshot.child("data").getValue(UserData.class);
                        String username= userData.getUsername();
                        String profilePhoto=userData.getProfilePhoto();
                        String id=dataSnapshot.getKey();
                        boolean isFriend=false;
                        for(String friendId:mFriendsIdList){
                            if(friendId.equals(id)){
                                isFriend=true;
                            }
                        }

                        mTemporaryList.add(new SinglePersonSearchItem(id,username,profilePhoto,isFriend));

                        mAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
        mDatabase.child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mAdapter.notifyDataSetChanged();
                        mAdapter.updateActualList();



                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    //ON ADD BUTTON FROM LIST
    public void onAddClick(int position){//tutaj dac id i name

        String id= mAdapter.getmList().get(position).getPersonId();
        String name= mAdapter.getmList().get(position).getPersonText();
        boolean isFriend=mAdapter.getmList().get(position).isFriend();
        Log.d("ddddddddIDIDIDNANA",name);

        if(isFriend){


            mDatabase.child("users").child(myId)
                    .child("friends").child(id).removeValue();


        }else{

        mDatabase.child("users").child(myId)
        .child("friends").child(id).setValue(name);


        }
        downloadListFromDatabase();
        Log.d("ddddddddMMDMD", String.valueOf(mAdapter.isIfOnlyFriends()));








    }
    //ONITEM FROM LIST
    @Override
    public void onItemClick(int position) {

        newIntent= new Intent(this, ChatActivity.class);

        newIntent.putExtra("id", mAdapter.getmList().get(position).getPersonId());



        mChatClickedDatabaseReference= mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages").child(mAdapter.getmList().get(position).getPersonId());
        mChatClickedDatabaseReference
                .addValueEventListener(mOnChatIdValueListener);

    }

    private void cleanUp(){

    }

    @Override
    protected void onStop() {
        super.onStop();
        cleanUp();
    }
}
