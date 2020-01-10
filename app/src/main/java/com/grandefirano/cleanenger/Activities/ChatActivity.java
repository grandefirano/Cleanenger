package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.adapter.ChatListAdapter;
import com.grandefirano.cleanenger.singleItems.SingleMessage;

public class ChatActivity extends AppCompatActivity {

    TextView mTextView;
    String mIdOfChatPerson;
    EditText mMessageInput;
    String mchatId;
    String mNameOfChatPerson;

    private ChatListAdapter mAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mChatRef;
    private ListView mChatListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        mTextView=findViewById(R.id.Id);
        mMessageInput=findViewById(R.id.messageInput);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        Intent intent=getIntent();
        mIdOfChatPerson=intent.getStringExtra("id");
        mNameOfChatPerson=intent.getStringExtra("username");

        mTextView.setText(mIdOfChatPerson);

         mchatId=intent.getStringExtra("chatId");

        mChatRef=mDatabase.child("chats").child(mchatId);
        mAdapter= new ChatListAdapter(ChatActivity.this, mChatRef,mAuth.getCurrentUser().getUid(),mNameOfChatPerson );
                            mChatListView.setAdapter(mAdapter);

                        checkIfRead();


        //FOR WRITING USER
//        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
//                .child("main_screen_messages").child(mIdOfChatPerson)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if(!dataSnapshot.exists()){
//                            //If not exists
//                            mchatId= UUID.randomUUID().toString();
//
//                        }else{
//                            //If exists
//                            mchatId=dataSnapshot.getValue().toString();
//
//                            DatabaseReference refy= mDatabase.child("chats").child(mchatId);
//                            mAdapter= new ChatListAdapter(ChatActivity.this, refy,mAuth.getCurrentUser().getUid(),mNameOfChatPerson );
//                            mChatListView.setAdapter(mAdapter);
//
//                        }
//                        mChatRef=mDatabase.child("chats").child(mchatId);
//
//                        checkIfRead();
//
//
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) { }
//                });

        //setReference to Chat


    }


    public void sendMessage(View view){



        String textOfMessage=mMessageInput.getText().toString();

        //SENDING USER
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("main_screen_messages")
                .child(mIdOfChatPerson).setValue(mchatId);
        //RECEIVING USER

        mDatabase.child("users").child(mIdOfChatPerson).child("main_screen_messages")
                .child(mAuth.getCurrentUser().getUid()).setValue(mchatId);


//        if(!mAuth.getCurrentUser().getUid().equals(mIdOfChatPerson)) {
//            mDatabase.child("users").child(mIdOfChatPerson).child("main_screen_messages")
//                    .child(mAuth.getCurrentUser().getUid()).setValue(messageBlock);
//        }
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

        public LastMessage() {
        }

        public boolean isifRead() {
            return isifRead;
        }
    }


    private void checkIfRead(){
        if(mchatId!=null) {

             mDatabase.child("chats").child(mchatId).child("last_message").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.child("uId").getValue().toString().equals(mAuth.getCurrentUser().getUid())
                    || mAuth.getCurrentUser().getUid().equals(mIdOfChatPerson)) {
                        mDatabase.child("chats").child(mchatId).child("last_message").child("ifRead").setValue(true);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO:???
        checkIfRead();


        //





    }
}
