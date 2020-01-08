package com.grandefirano.cleanenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.EventListener;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    TextView mTextView;
    String mIdOfChatPerson;
    EditText mMessageInput;
    String mchatId;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mChatRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        mTextView=findViewById(R.id.Id);
        mMessageInput=findViewById(R.id.messageInput);

        Intent intent=getIntent();
        mIdOfChatPerson=intent.getStringExtra("id");

        mTextView.setText(mIdOfChatPerson);


        //FOR WRITING USER
        mDatabase.child("users").child(mAuth.getUid())
                .child("main_screen_messages").child(mIdOfChatPerson)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            //If not exists
                            mchatId= UUID.randomUUID().toString();

                        }else{
                            //If exists
                            mchatId=dataSnapshot.getValue().toString();

                        }
                        mChatRef=mDatabase.child("chats").child(mchatId);

                        checkIfRead();



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //setReference to Chat


    }
    private void forRetrieving(){
        //FOR RETRIEVING USER

    }


    public void sendMessage(View view){


        //Mesage Block potrzebny??
        String textOfMessage=mMessageInput.getText().toString();

        MessageBlock messageBlock= new MessageBlock(textOfMessage,12434,mchatId);

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

        LastMessage lastMessage=new LastMessage(singleMessage.uId,singleMessage.message,false);

        mChatRef.child("last_message").setValue(lastMessage);

    }


    private class SingleMessage{
        private String uId;
        private String message;

        public SingleMessage(String uId, String message) {
            this.uId = uId;
            this.message = message; }
        public SingleMessage() { }

        public String getuId() { return uId; }
        public String getMessage() { return message; }
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

    private class MessageBlock{
        private int date;
        private String message;
        private boolean ifRead=false;
        private String chatId;



        public MessageBlock(String message, int date,String chatId) {
            this.date = date;
            this.message = message;
            this.chatId=chatId;

        }

        public boolean isIfRead() {
            return ifRead;
        }

        public MessageBlock() {
        }

        public int getDate() {
            return date;
        }

        public String getMessage() {
            return message;
        }

        public String getChatId() {
            return chatId;
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



    }
}
