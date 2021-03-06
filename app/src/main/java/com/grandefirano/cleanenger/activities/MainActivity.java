package com.grandefirano.cleanenger.activities;

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

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.grandefirano.cleanenger.notifications.Token;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.adapters.StoryAdapter;
import com.grandefirano.cleanenger.login_register.Login;
import com.grandefirano.cleanenger.adapters.MainListAdapter;
import com.grandefirano.cleanenger.single_items.SingleMainScreenMessage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainListAdapter.OnItemListener, StoryAdapter.OnSnapClickListener{


    public static final String SHARED_PREFS ="user_prefs" ;
    public static final String MY_NAME ="my_name" ;

    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private String myId;
    private DatabaseReference mDatabase;
    private DatabaseReference mainScreenMessagesReference;


    private long backPressedTime;


    private StoryAdapter mStoryAdapter;
    private List<String> mStoryList=new ArrayList<>();

    private RecyclerView.Adapter mAdapter;
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<SingleMainScreenMessage> listItems= new ArrayList<>();
    ArrayList<String> chatIdList= new ArrayList<>();
    ArrayList<String> usernameList=new ArrayList<>();

    ChildEventListener mOnReceivedSnapsListener=new ChildEventListener() {
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
    };

    ChildEventListener mOnMainScreenMessageListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            SingleMainScreenMessage singleMainScreenMessage=
                    dataSnapshot.getValue(SingleMainScreenMessage.class);
            if(singleMainScreenMessage!=null) {
                String chatId = singleMainScreenMessage.getChatId();

                idList.add(String.valueOf(dataSnapshot.getKey()));
                chatIdList.add(chatId);

                Log.d("CHANGE",idList.get(idList.size()-1));
                Log.d("CHANGE",chatIdList.get(chatIdList.size()-1));


                mAdapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            SingleMainScreenMessage singleMainScreenMessage=
                    dataSnapshot.getValue(SingleMainScreenMessage.class);
            if(singleMainScreenMessage!=null) {
                String chatId = singleMainScreenMessage.getChatId();
                String uId=String.valueOf(dataSnapshot.getKey());
                idList.remove(uId);
                chatIdList.remove(chatId);
                idList.add(uId);
                chatIdList.add(chatId);

                mAdapter.notifyDataSetChanged();
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };



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

            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    //MAIN PART

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FIREBASE
        mAuth=FirebaseAuth.getInstance();
        mFirebaseUser = mAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            goToLogin();
        }else {
            myId = mFirebaseUser.getUid();
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mainScreenMessagesReference = mDatabase.child("users")
                .child(myId)
                .child("main_screen_messages");

        //FIND VIEWS
        RecyclerView messagesRecyclerView = findViewById(R.id.searchPeopleRecyclerView);
        RecyclerView storyRecyclerView = findViewById(R.id.storyRecycleView);


        //DOWNLOAD DATA
        downloadMyName(myId);
        downloadListFromDatabase();
        listOfSnapsPerson();

        //SET RECYCLER VIEW FOR MESSAGE
        mAdapter = new MainListAdapter(getApplicationContext(),
                myId, this, chatIdList, idList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(mAdapter);

        //SET RECYCLER VIEW FOR STORY
        mStoryAdapter = new StoryAdapter(getApplicationContext(),
                mStoryList, this);
        LinearLayoutManager mStoryLinearLayoutManager = new LinearLayoutManager(
                getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);

        storyRecyclerView.setHasFixedSize(true);
        storyRecyclerView.setLayoutManager(mStoryLinearLayoutManager);
        storyRecyclerView.setAdapter(mStoryAdapter);


        //UPDATE FIREBASE NOTIFICATIONS TOKEN
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()){
                            if(task.getResult()!=null)
                                updateToken(task.getResult().getToken());
                        }
                    }
                });
    }

    private void downloadMyName(String id){

        //DOWNLOAD CURRENT USER NAME
        mDatabase.child("users").child(id).child("data")
                .child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username=dataSnapshot.getValue(String.class);

                if(username!=null) {

                    SharedPreferences sharedPreferences =
                            getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(MY_NAME, username);
                    editor.apply();
                }else
                    logOut();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void listOfSnapsPerson(){

        mStoryList.clear();

        //DOWNLOAD RECEIVED SNAPS
        mDatabase.child("users").child(myId).child("snaps")
                .addChildEventListener(mOnReceivedSnapsListener);
    }

    private void downloadListFromDatabase(){

        listItems.clear();
        idList.clear();
        chatIdList.clear();
        usernameList.clear();

        //DOWNLOAD USERS ORDERED BY DATE
        mainScreenMessagesReference.orderByChild("date")
                .addChildEventListener(mOnMainScreenMessageListener);
    }

    public void takeAPhoto(View view){

        Intent intent= new Intent(this,SendPhotoActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mFirebaseUser==null)
            goToLogin();
    }

    public void logOut(){

        //DELETE SHARED PREFERENCES WHEN LOG OUT
        SharedPreferences preferences =getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        //LOGOUT FROM DATABASE
        for (UserInfo userInfo : mFirebaseUser.getProviderData()) {
            if (userInfo.getProviderId().equals("facebook.com")) {
                LoginManager.getInstance().logOut();
            }
        }
        mAuth.signOut();

        //OPEN LOGIN ACTIVITY
        goToLogin();
    }

    private void goToLogin(){

        Intent intent= new Intent(this,Login.class);
        finish();
        startActivity(intent);
    }

    //ON CHAT ITEM CLICK
    @Override
    public void onItemClick(int position) {
        Intent intent= new Intent(this, ChatActivity.class);
        intent.putExtra("id", idList.get(position));
        intent.putExtra("chatId",chatIdList.get(position));
        finish();
        startActivity(intent);
    }

    //ON SNAP ITEM CLICK
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
            startActivity(a);
        }else{

            Toast.makeText(getApplicationContext(),"Press back again to exit",
                    Toast.LENGTH_SHORT).show();
        }
        backPressedTime=System.currentTimeMillis();

    }


    public void updateToken(String token){

        //UPDATE CURRENT USER TOKEN IF NECESSARY
        DatabaseReference reference=FirebaseDatabase.
                getInstance().getReference("tokens");
        Token mToken= new Token(token);
        reference.child(myId).setValue(mToken);
    }

    @Override
    protected void onDestroy() {
        //REMOVE LISTENERS
        super.onDestroy();
        if(mainScreenMessagesReference!=null)
            mainScreenMessagesReference.orderByChild("date")
                    .removeEventListener(mOnMainScreenMessageListener);

        if(mDatabase!=null)
            mDatabase.child("users").child(myId).child("snaps")
                    .addChildEventListener(mOnReceivedSnapsListener);
    }
}
