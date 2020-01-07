package com.grandefirano.cleanenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.login.Login;
import com.grandefirano.cleanenger.login.MainListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainListAdapter.OnItemListener {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private RecyclerView mMessagesRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<SingleMessageFeedItem> listItems= new ArrayList<>();

    String TAG="CHECK_LOG_MAIN";


    //MENU THREE DOTS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater= new MenuInflater(this);
        menuInflater.inflate(R.menu.clean_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.account){
            Intent intent= new Intent(this,AccountActivity.class);
            finish();
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.find_people){
            Intent intent=new Intent(this, FindPeopleActivity.class);
            finish();
            startActivity(intent);

        }
        else if(item.getItemId()==R.id.logout){
            mAuth.signOut();
            Intent intent= new Intent(this,Login.class);
            finish();
            startActivity(intent);

            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                Log.d(TAG,"There is no user");

            }
        }

        return super.onOptionsItemSelected(item);
    }

    //MAIN PART

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mMessagesRecyclerView=findViewById(R.id.searchPeopleRecyclerView);

        //DOWNLOAD USERS
        downloadListFromDatabase();



        mMessagesRecyclerView=findViewById(R.id.searchPeopleRecyclerView);
        mMessagesRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);
        mAdapter= new MainListAdapter(getApplicationContext(),listItems,this);

        mMessagesRecyclerView.setLayoutManager(mLayoutManager);
        mMessagesRecyclerView.setAdapter(mAdapter);

        //ONCLICK





    }

    private void downloadListFromDatabase(){
        listItems.clear();
        idList.clear();


        mDatabase.child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        emails.add((String) dataSnapshot.child("from").getValue());
//                              TODO:
                        //tODo:BEZ NAZWY DANE W BAZIE
                            makeListItem(dataSnapshot.getKey(),
                                    String.valueOf(dataSnapshot.child("message").getValue()),
                                    (boolean)dataSnapshot.child("ifRead").getValue());

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

    private void makeListItem(String uId, final String message, final boolean ifRead){



        mDatabase.child("users").child(uId).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String username= (String) dataSnapshot.child("username").getValue();
               String profilePhoto=(String)dataSnapshot.child("profile_photo").getValue();




                //TODO: ZDJECIE
               listItems.add(new SingleMessageFeedItem(profilePhoto,
                        username,
                        message,
                        ifRead));
                mAdapter.notifyDataSetChanged();

                Log.i("ssdfdfa",String.valueOf(listItems.size()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }


    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        //TODO: przelozyc do create
        if(currentUser==null) {
            goToLogin();
        }else{
            //TODO: IF USER IS NOT NULL
            Log.d(TAG,currentUser.toString());









        }
    }

    private void goToLogin(){

        Intent intent= new Intent(this,Login.class);
        finish();
        startActivity(intent);

    }

    //ONITEMCLICK
    @Override
    public void onItemClick(int position) {

        Intent intent= new Intent(this, ChatActivity.class);
        intent.putExtra("id", idList.get(position));
        finish();
        startActivity(intent);
    }






}
