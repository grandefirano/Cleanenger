package com.grandefirano.cleanenger.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.adapters.ChatListAdapter;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.single_items.ChatData;


public class ChatOptionsActivity extends AppCompatActivity {

    //FIREBASE
    private DatabaseReference mChatDataReference;

    //VIEWS
    private RadioGroup mSizeRadioGroup;
    private CircleImageView mClickedColorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_options);

        mClickedColorView=findViewById(R.id.colorPickDefault);
        mSizeRadioGroup=findViewById(R.id.chatTextSizeRadioGroup);
        ImageView mCloseActivityView=findViewById(R.id.closeActivityImageView);
        TextView mSaveTextView=findViewById(R.id.saveTextView);
        FlexboxLayout mColorPicker=findViewById(R.id.colorPicker);

        //SET LISTENERS FOR EVERY COLOR
        for(int i=0;i<mColorPicker.getChildCount();i++ ){
            mColorPicker.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickColor(v);
                }
            });
        }
        //SET LISTENERS FOR TOOLBAR
        mCloseActivityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSaveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChatOptions();
            }
        });

        //INTENT
        Intent intent=getIntent();
        String mChatId=intent.getStringExtra("chatId");

        //FIREBASE
        mChatDataReference= FirebaseDatabase.getInstance().getReference()
                            .child("chats").child(mChatId).child("data");

        setDataFromDatabase();

    }

    private void setDataFromDatabase() {

        mChatDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ChatData chatData = dataSnapshot.getValue(ChatData.class);

                //IF DATA WAS SAVED PREVIOUSLY
                if (chatData != null) {
                    int color = chatData.getColor();
                    int textSize = chatData.getTextSize();

                    int colorViewId;
                    int idRadioButton;

                    //CHAT COLOR
                    switch (color) {
                        case ChatListAdapter.BLUE_CHAT_COLOR:
                            colorViewId = R.id.colorPickBlue;
                            break;
                        case ChatListAdapter.RED_CHAT_COLOR:
                            colorViewId = R.id.colorPickRed;
                            break;
                        case ChatListAdapter.YELLOW_CHAT_COLOR:
                            colorViewId = R.id.colorPickYellow;
                            break;
                        case ChatListAdapter.GREEN_CHAT_COLOR:
                            colorViewId = R.id.colorPickGreen;
                            break;
                        default:
                            colorViewId = R.id.colorPickDefault;
                    }

                    mClickedColorView=findViewById(colorViewId);

                    //TEXT SIZE
                    switch (textSize){
                        case 10:
                            idRadioButton=R.id.size10ChatRadioButton;
                            break;
                        case 12:
                            idRadioButton=R.id.size12ChatRadioButton;
                            break;
                        case 20:
                            idRadioButton=R.id.size20ChatRadioButton;
                            break;
                        case 24:
                            idRadioButton=R.id.size24ChatRadioButton;
                            break;
                        default:
                            idRadioButton=R.id.defaultSizeChatRadioButton;
                    }
                    mSizeRadioGroup.check(idRadioButton);

                }//end of "if saved before"
                mClickedColorView.setBorderWidth(10);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }


    public void pickColor(View view){
        //REMOVE BORDER of old one
        CircleImageView oldView=mClickedColorView;
        oldView.setBorderWidth(0);

        //SET BORDER of new one
        mClickedColorView=(CircleImageView)view;
        mClickedColorView.setBorderWidth(10);
    }

    public void saveChatOptions(){

        int radioSizeId=mSizeRadioGroup.getCheckedRadioButtonId();
        int chatColorId=mClickedColorView.getId();

        int colorToSave;
        int sizeToSave;

        switch(chatColorId){
            case R.id.colorPickBlue:
                colorToSave=ChatListAdapter.BLUE_CHAT_COLOR;
                break;
            case R.id.colorPickRed:
                colorToSave=ChatListAdapter.RED_CHAT_COLOR;
                break;
            case R.id.colorPickYellow:
                colorToSave=ChatListAdapter.YELLOW_CHAT_COLOR;
                break;
            case R.id.colorPickGreen:
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

        ChatData chatData=
                new ChatData(colorToSave,sizeToSave);
        mChatDataReference.setValue(chatData);

        finish();
    }
}
