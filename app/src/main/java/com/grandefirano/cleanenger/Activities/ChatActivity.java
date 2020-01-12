package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.UserData;
import com.grandefirano.cleanenger.adapter.ChatListAdapter;
import com.grandefirano.cleanenger.singleItems.SingleMessage;
import com.squareup.picasso.Picasso;

public class ChatActivity extends AppCompatActivity {

    String mIdOfChatPerson;
    EditText mMessageInput;
    String mchatId;
    //String mNameOfChatPerson;

    private ChatListAdapter mAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mChatRef;
    private ListView mChatListView;

    private CircleImageView mPersonImageView;
    private TextView mPersonNameTextView;

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

        //VIEW FINDING


        mMessageInput=findViewById(R.id.messageInput);
        mChatListView =findViewById(R.id.chat_list_view);
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
                Picasso.with(getApplicationContext()).load(userData.getProfilePhoto())
                        .fit()
                        .centerCrop()
                        .into(mPersonImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        //ADAPTER
        mAdapter= new ChatListAdapter(ChatActivity.this, mChatRef,
                mAuth.getCurrentUser().getUid(),null );//TODO::::

        mChatListView.setAdapter(mAdapter);






        changeReadStatus();
    }


    public void sendMessage(View view){

        String textOfMessage=mMessageInput.getText().toString();

        //SENDING USER
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("main_screen_messages")
                .child(mIdOfChatPerson).setValue(mchatId);
        //RECEIVING USER

        mDatabase.child("users").child(mIdOfChatPerson).child("main_screen_messages")
                .child(mAuth.getCurrentUser().getUid()).setValue(mchatId);


        //Message
        SingleMessage singleMessage= new SingleMessage(mAuth.getUid(),textOfMessage);
        mChatRef.child(String.valueOf(System.currentTimeMillis())).setValue(singleMessage);

        LastMessage lastMessage=new LastMessage(singleMessage.getuId(),singleMessage.getMessage(),false);

        mChatRef.child("last_message").setValue(lastMessage);

    }



    private class LastMessage extends SingleMessage{
        private boolean isifRead;

        public LastMessage(String uId,String message, boolean isRead) {
            super(uId, message);
            this.isifRead = isRead;
        }
        public LastMessage() { }
        public boolean isifRead() {
            return isifRead;
        }
    }


    private void changeReadStatus(){
        if(mchatId!=null) {

             mDatabase.child("chats").child(mchatId).child("last_message").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("uId").exists()) {
                        if (!dataSnapshot.child("uId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                                || mAuth.getCurrentUser().getUid().equals(mIdOfChatPerson)) {
                            mDatabase.child("chats").child(mchatId).child("last_message").child("ifRead").setValue(true);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        changeReadStatus();

    }
}
