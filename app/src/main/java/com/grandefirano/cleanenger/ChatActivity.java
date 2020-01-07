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


        mDatabase.child("users").child(mAuth.getUid())
                .child("main_screen_messages").child(mIdOfChatPerson).child("chatId")
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //setReference to Chat


    }


    public void sendMessage(View view){


        //Mesage Block potrzebny??
        String textOfMessage=mMessageInput.getText().toString();

        MessageBlock messageBlock= new MessageBlock(textOfMessage,12434,mchatId);

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("main_screen_messages").child(mIdOfChatPerson).setValue(messageBlock);
        //Message
        SingleMessage singleMessage= new SingleMessage(mAuth.getUid(),textOfMessage);
        mChatRef.child(String.valueOf(System.currentTimeMillis())).setValue(singleMessage);
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

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages").child(mIdOfChatPerson).child("ifRead").setValue(true);

    }
}
