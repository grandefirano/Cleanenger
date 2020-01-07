package com.grandefirano.cleanenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class ChatActivity extends AppCompatActivity {

    TextView mTextView;
    String mIdOfChatPerson;
    EditText mMessageInput;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
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
    }


    public void sendMessage(View view){
        MessageBlock messageBlock= new MessageBlock(mMessageInput.getText().toString(),12434);

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("main_screen_messages").child(mIdOfChatPerson).setValue(messageBlock);

    }

    private class MessageBlock{
        private int date;
        private String message;
        private boolean ifRead=false;



        public MessageBlock(String message, int date) {
            this.date = date;
            this.message = message;

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


    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase.child("users").child(mAuth.getCurrentUser().getUid())
                .child("main_screen_messages").child(mIdOfChatPerson).child("ifRead").setValue(true);

    }
}
