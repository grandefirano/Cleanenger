package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.grandefirano.cleanenger.Notifications.Token;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.Adapter.StoryAdapter;
import com.grandefirano.cleanenger.Login.Login;
import com.grandefirano.cleanenger.Adapter.MainListAdapter;
import com.grandefirano.cleanenger.SingleItems.SingleMessageFeedItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainListAdapter.OnItemListener, StoryAdapter.OnSnapClickListener{


    public static final String SHARED_PREFS ="user_prefs" ;
    public static final String MY_NAME ="my_name" ;
    FirebaseAuth mAuth;
    String myId;
    DatabaseReference mDatabase;
    DatabaseReference mainScreenMessagesReference;


    private long backPressedTime;



    private RecyclerView mStoryRecyclerView;
    private StoryAdapter mStoryAdapter;
    private List<String> mStoryList=new ArrayList<>();

    private RecyclerView mMessagesRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<SingleMessageFeedItem> listItems= new ArrayList<>();
    ArrayList<String> chatIdList= new ArrayList<>();
    ArrayList<String> usernameList=new ArrayList<>();



    ChildEventListener mOnMainScreenMessageListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            DatabaseReference lastMessage=mDatabase.child("chats").child(dataSnapshot.getValue().toString()).child("last_message");

            idList.add(String.valueOf(dataSnapshot.getKey()));
            chatIdList.add(String.valueOf(dataSnapshot.getValue()));
            mAdapter.notifyDataSetChanged();

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



    String TAG="CHECK_LOG_MAIN";

    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater= getMenuInflater();
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
            Intent intent=new Intent(this, FindPeopleActivity.class);
            finish();
            startActivity(intent);

        }
        else if(item.getItemId()==R.id.logout){

            //DELETE SHARED PREFERENCES WHEN LOG OUT
            SharedPreferences preferences =getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();

            //LOGOUT FROM DATABASE
            mAuth.signOut();

            //OPEN LOGIN ACTIVITY
            Intent intent= new Intent(this,Login.class);
            finish();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    //MAIN PART

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();
        myId=mAuth.getUid();

        mDatabase=FirebaseDatabase.getInstance().getReference();
        mainScreenMessagesReference=mDatabase.child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages");


        mMessagesRecyclerView=findViewById(R.id.searchPeopleRecyclerView);

        mStoryRecyclerView=findViewById(R.id.storyRecycleView);

        //TODO:
        //DOWNLOAD MY DATA
        downloadMyName(myId);


        //DOWNLOAD USERS
        downloadListFromDatabase();
        listOfSnapsPerson();


        mMessagesRecyclerView.setHasFixedSize(true);
        mLayoutManager= new LinearLayoutManager(this);


        mAdapter= new MainListAdapter(getApplicationContext(),listItems,this,chatIdList,idList);





        mMessagesRecyclerView.setLayoutManager(mLayoutManager);
        mMessagesRecyclerView.setAdapter(mAdapter);

        mStoryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mStoryLinearLayoutManager= new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,false);
        mStoryRecyclerView.setLayoutManager(mStoryLinearLayoutManager);


        mStoryAdapter=new StoryAdapter(getApplicationContext(),mStoryList,this);
        mStoryRecyclerView.setAdapter(mStoryAdapter);


        //UPDATE FIREBASE NOTIFICATIONS TOKEN
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void downloadMyName(String id){


        mDatabase.child("users").child(id).child("data").child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(MY_NAME,dataSnapshot.getValue().toString());
                editor.commit();
                Log.d("ddddddmINAME",dataSnapshot.getValue().toString());
                Log.d("ddddddmINAME","ddd");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void listOfSnapsPerson(){

        mStoryList.clear();

        mDatabase.child("users").child(myId).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                mStoryList.add(dataSnapshot.getKey());
                mStoryAdapter.notifyDataSetChanged();
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

    private void downloadListFromDatabase(){
        listItems.clear();
        idList.clear();
        chatIdList.clear();
        usernameList.clear();

        mainScreenMessagesReference.addChildEventListener(mOnMainScreenMessageListener);

    }

//    private void findLastMessage(DatabaseReference reference, final String uId){
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String mess=String.valueOf(dataSnapshot.child("message").getValue());
//                String hisId=String.valueOf(dataSnapshot.child("uId").getValue());
//                boolean ifRead=(boolean)dataSnapshot.child("ifRead").getValue();
//                boolean ifMe=hisId.equals(myId);
//
//                if(ifMe){
//                    mess="Me: "+mess;
//                    if(ifRead){
//                        mess="[Read]"+mess;
//                    }
//                }
//
//                makeListItem(uId,mess,
//                        ifRead,ifMe);
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
//    }
//
//    public void makeListItem(String uId, final String message, final boolean ifRead, final boolean ifMe){
//
//        mDatabase.child("users").child(uId).child("data").addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               String username= (String) dataSnapshot.child("username").getValue();
//               String profilePhoto=(String)dataSnapshot.child("profilePhoto").getValue();
//
//
//
//                usernameList.add(username);
//               mTemporaryList.add(new SingleMessageFeedItem(profilePhoto,
//                        username,
//                        message,
//                        ifRead));
//
//                mAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });
//
//
//    }
    public void takeAPhoto(View view){

        Intent intent= new Intent(this,SendPhotoActivity.class);
        finish();
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseUser currentUser = mAuth.getCurrentUser();

        //TODO: przelozyc do create
        if(currentUser==null) {
            goToLogin();
        }else{ }
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
        intent.putExtra("chatId",chatIdList.get(position));
        finish();
        startActivity(intent);
    }

    @Override
    public void onSnapClick(int position) {
        Intent intent= new Intent(this, ShowStoryActivity.class);
        intent.putExtra("id",mStoryList.get(position));
        finish();
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()){
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //???? finish();
            startActivity(a);

        }else{
            Toast.makeText(getApplicationContext(),"Press back again to exit",Toast.LENGTH_SHORT).show();

        }

        backPressedTime=System.currentTimeMillis();

    }


    public void updateToken(String token){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("tokens");
        Token mToken= new Token(token);
        reference.child(myId).setValue(mToken);
    }

}
