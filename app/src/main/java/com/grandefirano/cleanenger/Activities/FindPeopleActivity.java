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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.grandefirano.cleanenger.Activities.ChatActivity;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.adapter.FindPeopleAdapter;
import com.grandefirano.cleanenger.messages.SinglePersonSearchItem;

import java.util.ArrayList;
import java.util.UUID;

public class FindPeopleActivity extends AppCompatActivity implements FindPeopleAdapter.OnItemListener {


    RecyclerView mRecyclerView;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private FindPeopleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<SinglePersonSearchItem> listItems= new ArrayList<>();
    ArrayList<String> usernameList=new ArrayList<>();
    private Intent newIntent;
    private Context mContext;


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
                Log.d("ddddd",newText);
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

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mContext=getApplicationContext();


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);

        Intent intent=getIntent();
       ///////


        downloadListFromDatabase();


        //mAdapter= new FindPeopleAdapter(getApplicationContext(),listItems,this);









    }



    private void downloadListFromDatabase(){
        listItems.clear();
        idList.clear();
        usernameList.clear();


        mDatabase.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAdapter= new FindPeopleAdapter(mContext,listItems,FindPeopleActivity.this);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);


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
                        Log.d("rrrr","ffffff");
                        String username= (String) dataSnapshot.child("data").child("username").getValue();
                        String profilePhoto=(String)dataSnapshot.child("data").child("profile_photo").getValue();

                        listItems.add(new SinglePersonSearchItem(profilePhoto,username));
                        //TODO:SDKFLDSKFL
                        //mAdapter.notifyDataSetChanged();

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


    @Override
    public void onItemClick(int position) {

        newIntent= new Intent(this, ChatActivity.class);
        newIntent.putExtra("id", idList.get(position));
        newIntent.putExtra("username",usernameList.get(position));


        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages").child(idList.get(position))
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


}
