package com.grandefirano.cleanenger.Activities;

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
import com.grandefirano.cleanenger.Activities.AccountActivity;
import com.grandefirano.cleanenger.Activities.ChatActivity;
import com.grandefirano.cleanenger.Activities.FindPeopleActivity;
import com.grandefirano.cleanenger.DownloaderController;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.login.Login;
import com.grandefirano.cleanenger.adapter.MainListAdapter;
import com.grandefirano.cleanenger.messages.SingleMessageFeedItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainListAdapter.OnItemListener {

    FirebaseAuth mAuth;
    String myId;
    DatabaseReference mDatabase;
    private RecyclerView mMessagesRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<SingleMessageFeedItem> listItems= new ArrayList<>();
    ArrayList<String> chatIdList= new ArrayList<>();
    ArrayList<String> usernameList=new ArrayList<>();

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
            Intent intent= new Intent(this, AccountActivity.class);
            finish();
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.find_people){
//            DownloaderController downloaderController=new DownloaderController(this);
//            downloaderController.downloadListForFindPeople();
            finish();
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
        myId=mAuth.getUid();

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
        chatIdList.clear();
        usernameList.clear();


        mDatabase.child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                        emails.add((String) dataSnapshot.child("from").getValue());
//                              TODO:
                        //tODo:BEZ NAZWY DANE W BAZIE
                        DatabaseReference lastMessage=mDatabase.child("chats").child(dataSnapshot.getValue().toString()).child("last_message");

                        idList.add(String.valueOf(dataSnapshot.getKey()));
                        chatIdList.add(String.valueOf(dataSnapshot.getValue()));

                        findLastMessage(lastMessage,dataSnapshot.getKey());

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

    private void findLastMessage(DatabaseReference reference, final String uId){
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mess=String.valueOf(dataSnapshot.child("message").getValue());
                String hisId=String.valueOf(dataSnapshot.child("uId").getValue());
                if(hisId.equals(myId)){
                    mess="Me: "+mess;
                }

                makeListItem(uId,mess,
                        (boolean)dataSnapshot.child("ifRead").getValue());
               Log.d("ddddd",uId);
               Log.d("ddddd",String.valueOf(dataSnapshot.child("message").getValue()));
               Log.d("ddddd",dataSnapshot.child("ifRead").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void makeListItem(String uId, final String message, final boolean ifRead){




        mDatabase.child("users").child(uId).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               String username= (String) dataSnapshot.child("username").getValue();
               String profilePhoto=(String)dataSnapshot.child("profile_photo").getValue();

                usernameList.add(username);




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
        intent.putExtra("username",usernameList.get(position));
        intent.putExtra("chatId",chatIdList.get(position));
        finish();
        startActivity(intent);
    }






}
