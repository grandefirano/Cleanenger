package com.grandefirano.cleanenger.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.grandefirano.cleanenger.Adapter.ChatListAdapter;
import com.grandefirano.cleanenger.R;

public class ChatOptionsActivity extends AppCompatActivity {

    private RadioGroup mColorRadioGroup;
    private RadioGroup mSizeRadioGroup;

    private DatabaseReference mChatDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_options);

        ImageView mCloseActivityView=findViewById(R.id.closeActivityImageView);
        LinearLayout mSaveLinearLayout=findViewById(R.id.saveLinearLayout);

        mColorRadioGroup=findViewById(R.id.chatColorRadioGroup);
        mSizeRadioGroup=findViewById(R.id.chatTextSizeRadioGroup);

        mCloseActivityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSaveLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChatOptions();
            }
        });



        Intent intent=getIntent();
        String mChatId=intent.getStringExtra("chatId");

        mChatDataReference= FirebaseDatabase.getInstance().getReference()
                            .child("chats").child(mChatId).child("data");

    }

    public void saveChatOptions(){
        int radioColorId=mColorRadioGroup.getCheckedRadioButtonId();
        int radioSizeId=mSizeRadioGroup.getCheckedRadioButtonId();

        int colorToSave;
        int sizeToSave;

        switch(radioColorId){

            case R.id.blueColorChatRadioButton:
                colorToSave=ChatListAdapter.BLUE_CHAT_COLOR;
                break;
            case R.id.redColorChatRadioButton:
                colorToSave=ChatListAdapter.RED_CHAT_COLOR;
                break;
            case R.id.yellowColorChatRadioButton:
                colorToSave=ChatListAdapter.YELLOW_CHAT_COLOR;
                break;
            case R.id.greenColorChatRadioButton:
                colorToSave=ChatListAdapter.GREEN_CHAT_COLOR;
                break;
            default:
                colorToSave=ChatListAdapter.DEFAULT_CHAT_COLOR;

        }


        switch (radioSizeId){
            case R.id.size10ChatRadioButton:
                sizeToSave=10;
                break;
            case R.id.size12ChatRadioButton:
                sizeToSave=12;
                break;
            case R.id.size20ChatRadioButton:
                sizeToSave=20;
                break;
            case R.id.size24ChatRadioButton:
                sizeToSave=24;
                break;
            default:
                sizeToSave=16;
        }


        mChatDataReference.child("color").setValue(colorToSave);

        mChatDataReference.child("textSize").setValue(sizeToSave);
        finish();
    }
}
