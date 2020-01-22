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
import com.grandefirano.cleanenger.Adapter.FindPeopleAdapter;
import com.grandefirano.cleanenger.SingleItems.SinglePersonSearchItem;
import com.grandefirano.cleanenger.SingleItems.UserData;

import java.util.ArrayList;
import java.util.UUID;

public class FindPeopleActivity extends AppCompatActivity implements FindPeopleAdapter.OnItemListener, FindPeopleAdapter.OnRightButtonListener {


    //FIREBASE
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mChatClickedDatabaseReference;

    //CURRENT USER ID
    private String myId;

    //LISTS OF USERS
    private ArrayList<String> mFriendsIdList= new ArrayList<>();
    private ArrayList<SinglePersonSearchItem> mTemporaryList=new ArrayList<>();

    //VIEWS
    private RecyclerView mRecyclerView;
    private Switch mSwitch;

    //RECYCLERVIEW IMPLEMENTS
    private FindPeopleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //INTENT AND CONTEXT
    private Intent newIntent;
    private Context mContext;


    //WHEN CLICKED CHAT TO GET CHAT ID IF EXISTS
    ValueEventListener mOnChatIdValueListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            String mChatId;
            if(!dataSnapshot.exists()){
                //IF NOT EXISTS
                mChatId= UUID.randomUUID().toString();
            }else{
                //IF EXISTS
                mChatId=dataSnapshot.getValue().toString();
            }
            newIntent.putExtra("chatId",mChatId);
            startActivity(newIntent);

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    //IF FRIENDS CHECKED LISTENER
    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(isChecked)
                mAdapter.showOnlyFriends(true);
            else
                mAdapter.showOnlyFriends(false);

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);

        MenuItem searchItem=menu.findItem(R.id.action_search);

        //SET SEARCHVIEW
        SearchView mSearchView= (SearchView) searchItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
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

        //FIREBASE AND ID
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        myId=mAuth.getCurrentUser().getUid();

        //CONTEXT
        mContext=getApplicationContext();

        //VIEWS
        mRecyclerView=findViewById(R.id.searchPeopleRecyclerView);
        mSwitch=findViewById(R.id.switchIfFriends);

        mSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);

        //IMPLEMENTATIONS FOR RECYCLERVIEW
        mLayoutManager= new LinearLayoutManager(this);
        mAdapter= new FindPeopleAdapter(mContext,mTemporaryList,FindPeopleActivity.this,FindPeopleActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        //DOWNLOAD FRIENDS AND ALL USERS LISTS
        downloadListFromDatabase();

    }


    private void downloadListFromDatabase(){

        //TEMPORARY LIST OF ALL USERS
        mTemporaryList.clear();

        //ADD FRIENDS
        mDatabase.child("users").child(myId).child("friends").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //INIT AND WHEN ADD FRIEND
                String friendId=dataSnapshot.getKey();
                mFriendsIdList.add(friendId);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                //WHEN REMOVE FRIEND
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

                        //CHECK IF IT'S ME
                        if(!id.equals(myId)) {
                            //CHECKING IF PERSON IS FRIEND
                            boolean isFriend = false;
                            for (String friendId : mFriendsIdList)
                                if (friendId.equals(id)) isFriend = true;

                            //ADDING PERSON DATA TO LIST
                            mTemporaryList.add(new SinglePersonSearchItem(id, username, profilePhoto, isFriend));
                            mAdapter.notifyDataSetChanged();
                        }
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
        //UPDATE LIST AFTER ALL USERS ARE ADDED (ValueListener is called after ChildListener)
        mDatabase.child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        mAdapter.updateActualList();
                        mAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

    }

    //ON ADD FRIEND CLICK
    public void onRightButtonClick(int position){

        String id= mAdapter.getmList().get(position).getPersonId();
        String name= mAdapter.getmList().get(position).getPersonText();
        boolean isFriend=mAdapter.getmList().get(position).isFriend();


        if(isFriend){
            mDatabase.child("users").child(myId)
                    .child("friends").child(id).removeValue();

        }else{

        mDatabase.child("users").child(myId)
        .child("friends").child(id).setValue(name);


        }
        downloadListFromDatabase();




    }
    //ONITEM FROM LIST
    @Override
    public void onItemClick(int position) {

        newIntent= new Intent(this, ChatActivity.class);

        newIntent.putExtra("id", mAdapter.getmList().get(position).getPersonId());



        mChatClickedDatabaseReference= mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages").child(mAdapter.getmList().get(position).getPersonId());
        mChatClickedDatabaseReference
                .addListenerForSingleValueEvent(mOnChatIdValueListener);

    }

    private void cleanUp(){

    }

    @Override
    protected void onStop() {
        super.onStop();
        cleanUp();
    }
}
