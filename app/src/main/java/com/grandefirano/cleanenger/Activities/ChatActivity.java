package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.UserData;
import com.grandefirano.cleanenger.adapter.ChatListAdapter;
import com.grandefirano.cleanenger.singleItems.SingleMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String mIdOfChatPerson;

    String profilePhoto;

    EditText mMessageInput;
    String mchatId;
    //String mNameOfChatPerson;

    boolean isOnTheBottom=true;


    private ChatListAdapter mAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mChatRef;
    private RecyclerView mChatRecycleView;

    private CircleImageView mPersonImageView;
    private TextView mPersonNameTextView;

    private ArrayList<SingleMessage> mMessagesList=new ArrayList<>();

    private ValueEventListener onLastMessageListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child("uId").exists()) {
                if (!dataSnapshot.child("uId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                        || mAuth.getCurrentUser().getUid().equals(mIdOfChatPerson)) {
                    Log.d("dddd","onDataLopata");
                    mDatabase.child("chats").child(mchatId).child("last_message").child("ifRead").setValue(true);
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    private ChildEventListener mListener= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            if(!dataSnapshot.getKey().equals("last_message")){
                SingleMessage message=dataSnapshot.getValue(SingleMessage.class);
                mMessagesList.add(message);
                mAdapter.notifyDataSetChanged();}
            mChatRecycleView.scrollToPosition(mAdapter.getItemCount()-1);
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //INTENT
        Intent intent=getIntent();
        mchatId=intent.getStringExtra("chatId");
        mIdOfChatPerson=intent.getStringExtra("id");


        //FIREBASE
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mChatRef=mDatabase.child("chats").child(mchatId);


        mChatRef.addChildEventListener(mListener);


        //VIEW FINDING


        mMessageInput=findViewById(R.id.messageInput);
        mChatRecycleView =findViewById(R.id.chat_recycle_view);
        mPersonNameTextView=findViewById(R.id.nameOfChatPersonTextView);
        mPersonImageView=findViewById(R.id.chatPersonImageView);

        Toolbar mToolbar=findViewById(R.id.chatToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });











        mDatabase.child("users").child(mIdOfChatPerson).child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData userData=dataSnapshot.getValue(UserData.class);

                mPersonNameTextView.setText(userData.getUsername());
                profilePhoto=userData.getProfilePhoto();
                Picasso.with(getApplicationContext()).load(userData.getProfilePhoto())
                        .fit()
                        .centerCrop()
                        .into(mPersonImageView);
                mAdapter.updateProfilePhoto(profilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        //ADAPTER
        mAdapter= new ChatListAdapter(this, mMessagesList,
                mAuth.getCurrentUser().getUid(),profilePhoto);

        mChatRecycleView.setAdapter(mAdapter);
        mChatRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mChatRecycleView.setLayoutManager(linearLayoutManager);

        mChatRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)){
                    isOnTheBottom=true;
                    Log.d("ddddddR","isOnbottom");
                }else{
                    isOnTheBottom=false;
                    Log.d("ddddddR","isNOtOnbottom");
                }
            }
        });
        //adjust recyclerview to keyboard
        mChatRecycleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override

            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                if(isOnTheBottom) {
                    Log.d("ddddd","scrollto poss");
                    mChatRecycleView.scrollToPosition(mAdapter.getItemCount() - 1);
                }

            }
        });




        changeReadStatus();
    }



    public void sendMessage(View view){

        String textOfMessage=mMessageInput.getText().toString();
        if(textOfMessage!=null && !textOfMessage.equals("")) {

            //SENDING USER
            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("main_screen_messages")
                    .child(mIdOfChatPerson).setValue(mchatId);
            //RECEIVING USER

            mDatabase.child("users").child(mIdOfChatPerson).child("main_screen_messages")
                    .child(mAuth.getCurrentUser().getUid()).setValue(mchatId);


            //Message




            SingleMessage singleMessage = new SingleMessage(mAuth.getUid(), textOfMessage, ServerValue.TIMESTAMP);
            LastMessage lastMessage = new LastMessage(singleMessage.getuId(), singleMessage.getMessage(), ServerValue.TIMESTAMP, false);


            mChatRef.push().setValue(singleMessage.toMap());
            //mChatRef.child(String.valueOf(ServerValue.TIMESTAMP)).setValue(singleMessage);

            //mChatRef.child(String.valueOf(System.currentTimeMillis())).setValue(singleMessage);


            mChatRef.child("last_message").setValue(lastMessage.toMap());
            mMessageInput.setText("");
            hideKeyboard(ChatActivity.this);


        }

    }
    private void hideKeyboard(Activity activity){
        //TODO::

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    public class LastMessage extends SingleMessage{
        private boolean isifRead;

        public LastMessage(String uId,String message,Map<String,String> dateCreated, boolean isRead) {
            super(uId, message,dateCreated);
            this.isifRead = isRead;
        }
        public Map<String, Object> toMap() {
           Map<String,Object> result=super.toMap();
           result.put("ifRead",isifRead);

            return result;
        }

        public LastMessage() { }
        public boolean isifRead() {
            return isifRead;
        }
    }


    private void changeReadStatus(){
        if(mchatId!=null) {

             mDatabase.child("chats").child(mchatId).child("last_message").addValueEventListener(onLastMessageListener);
                Log.d("dddd","changReadStat");
        }
    }

    @Override
    protected void onDestroy() {
        if(mDatabase!=null){
            Log.d("ddddd","istnieje");
        }else{
            Log.d("ddddd","niesitniejee");
        }
        mDatabase.child("chats").child(mchatId).child("last_message").removeEventListener(onLastMessageListener);
        mChatRef.removeEventListener(mListener);
        super.onDestroy();
        Log.d("ddddd","removing");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeReadStatus();

    }
}
