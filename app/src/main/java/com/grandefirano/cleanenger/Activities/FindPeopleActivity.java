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

import java.util.ArrayList;
import java.util.UUID;

public class FindPeopleActivity extends AppCompatActivity implements FindPeopleAdapter.OnItemListener, FindPeopleAdapter.OnAddButtonListener {


    RecyclerView mRecyclerView;
    Switch mSwitch;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private FindPeopleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<SinglePersonSearchItem> listItems= new ArrayList<>();
    ArrayList<String> friendsList= new ArrayList<>();
    private Intent newIntent;
    private Context mContext;

    ChildEventListener mChildEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
            Log.d("dddddd","childAdded");
            String username= (String) dataSnapshot.child("data").child("username").getValue();
            String profilePhoto=(String)dataSnapshot.child("data").child("profile_photo").getValue();
            String id=dataSnapshot.getKey();
            listItems.add(new SinglePersonSearchItem(profilePhoto,username,id));



        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    ValueEventListener onUsersValueListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.d("ddddd",String.valueOf(listItems.size()));
            mAdapter= new FindPeopleAdapter(mContext,listItems,FindPeopleActivity.this,FindPeopleActivity.this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mAdapter);
            Log.d("dddddd","datachange");


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



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


        mSwitch.setOnCheckedChangeListener(mOnCheckedChangeListener);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);



        Intent intent=getIntent();
       ///////


        downloadListFromDatabase();
        Log.d("dddddd","downloading;");


        //mAdapter= new FindPeopleAdapter(getApplicationContext(),listItems,this);









    }



    private void downloadListFromDatabase(){
        listItems.clear();

        friendsList.clear();


//        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("data").child("friends").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                dataSnapshot.getValue()
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        })
        mDatabase.child("users").addValueEventListener(onUsersValueListener);
        mDatabase.child("users")
                .addChildEventListener(mChildEventListener);

    }

    //ON ADD BUTTON FROM LIST
    public void onAddClick(int position){
        String id=listItems.get(position).getPersonId();
        String name=listItems.get(position).getPersonText();
        Log.d("dddd",name);
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("friends").child(id).setValue(name);

    }
    //ONITEM FROM LIST
    @Override
    public void onItemClick(int position) {

        newIntent= new Intent(this, ChatActivity.class);
        newIntent.putExtra("id", listItems.get(position).getPersonId());

        newIntent.putExtra("username",listItems.get(position).getPersonText());


        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages").child(listItems.get(position).getPersonId())
                .addValueEventListener(new ValueEventListener() {
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
                });

    }

    private void cleanUp(){
        mDatabase.child("users").removeEventListener(mChildEventListener);
        mDatabase.child("users").removeEventListener(onUsersValueListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cleanUp();
    }
}
