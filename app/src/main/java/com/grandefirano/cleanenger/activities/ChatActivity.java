package com.grandefirano.cleanenger.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.notifications.APIService;
import com.grandefirano.cleanenger.notifications.Client;
import com.grandefirano.cleanenger.notifications.Data;
import com.grandefirano.cleanenger.notifications.MyResponse;
import com.grandefirano.cleanenger.notifications.Sender;
import com.grandefirano.cleanenger.notifications.Token;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.single_items.ChatData;
import com.grandefirano.cleanenger.single_items.LastMessage;
import com.grandefirano.cleanenger.single_items.SingleMainScreenMessage;
import com.grandefirano.cleanenger.single_items.UserData;
import com.grandefirano.cleanenger.adapters.ChatListAdapter;
import com.grandefirano.cleanenger.single_items.SingleMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    //FIREBASE
    private DatabaseReference mDatabase;
    private DatabaseReference mChatRef;

    //NOTIFICATION SERVICE
    private APIService mAPIService;

    //VARIABLES
    private String mIdOfChatPerson;
    private String profilePhoto;
    private String mChatId;
    private String myId;
    private String myName;

    private boolean isOnTheBottom=false;
    private boolean ifMe;

    private ArrayList<SingleMessage> mMessagesList=new ArrayList<>();

    //VIEWS
    private CircleImageView mPersonImageView;
    private TextView mPersonNameTextView;
    private TextView mSeenStatus;
    private EditText mMessageInput;

    //RECYCLER VIEW
    private ChatListAdapter mAdapter;
    private RecyclerView mChatRecycleView;

    //LISTENERS
    private ValueEventListener mOnDataChangeListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            if(dataSnapshot.exists()) {
                ChatData data = dataSnapshot.getValue(ChatData.class);
                if(data!=null) {
                    int color = data.getColor();
                    int size = data.getTextSize();

                    mAdapter.setColor(color);
                    mAdapter.setTextSize(size);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    private ValueEventListener mOnChatPersonDataListener=(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            UserData userData=dataSnapshot.getValue(UserData.class);
            if(userData!=null) {
                String nameOfPerson = userData.getUsername();
                mPersonNameTextView.setText(nameOfPerson);

                profilePhoto = userData.getProfilePhoto();
                Picasso.with(getApplicationContext()).load(userData.getProfilePhoto())
                        .fit()
                        .centerCrop()
                        .into(mPersonImageView);
                mAdapter.updateProfilePhoto(profilePhoto);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    });
    private ValueEventListener mOnLastMessageListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            LastMessage lastMessage=dataSnapshot.getValue(LastMessage.class);

            if(lastMessage!=null) {
                setLastMessageStatus(lastMessage);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    private ChildEventListener mOnMessageListener= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            SingleMessage message=dataSnapshot.getValue(SingleMessage.class);
            mMessagesList.add(message);
            mAdapter.notifyDataSetChanged();

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

        //is always on the bottom of the recycler view
        //when chat is opened
        isOnTheBottom=true;

        //INTENT
        Intent intent=getIntent();
        mChatId =intent.getStringExtra("chatId");
        mIdOfChatPerson=intent.getStringExtra("id");

        //MY NAME (from Shared Preferences)
        SharedPreferences sharedPreferences=
                getSharedPreferences(MainActivity.SHARED_PREFS,Context.MODE_PRIVATE);
        myName=sharedPreferences
                .getString(MainActivity.MY_NAME,myId);

        //NOTIFICATIONS
        mAPIService= Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        //FIREBASE
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();

        mDatabase= FirebaseDatabase.getInstance().getReference();

        //Check if user exists
        if(user==null){
            finish();
        }else {
            myId = user.getUid();
        }

        //SET LISTENER
        if(mDatabase!=null) {
            mDatabase.child("users").child(mIdOfChatPerson).child("data")
                    .addValueEventListener(mOnChatPersonDataListener);
            if (mChatId != null) {
                mChatRef = mDatabase.child("chats").child(mChatId);
                mChatRef.child("data").addValueEventListener(mOnDataChangeListener);
                mChatRef.child("last_message").addValueEventListener(mOnLastMessageListener);
                mChatRef.child("messages").addChildEventListener(mOnMessageListener);
            }
        }

        //FIND VIEWS
        mSeenStatus=findViewById(R.id.seenStatus);
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

        //SET RECYCLER VIEW
        mAdapter= new ChatListAdapter(this, mMessagesList,
                myId,profilePhoto);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);

        mChatRecycleView.setAdapter(mAdapter);
        mChatRecycleView.setHasFixedSize(true);
        mChatRecycleView.setLayoutManager(linearLayoutManager);
        mChatRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //IF CANNOT SCROLL THEN IS ON THE BOTTOM
                isOnTheBottom= !recyclerView.canScrollVertically(1);

                //SET VISIBILITY OF SEEN STATUS
                //only needed if message
                //was written by current user
                if (ifMe) {
                    if(isOnTheBottom)
                        mSeenStatus.setVisibility(View.VISIBLE);
                    else
                        mSeenStatus.setVisibility(View.GONE);
                }
            }
        });

        //ADJUST RECYCLER VIEW TO THE KEYBOARD
        mChatRecycleView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override

            public void onLayoutChange(View v, int left, int top, int right,int bottom, int oldLeft, int oldTop,int oldRight, int oldBottom)
            {
                if(isOnTheBottom) {
                    mChatRecycleView.scrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        });

        changeReadStatus();
    }

    public void sendMessage(View view){

        final String textOfMessage=mMessageInput.getText().toString();
        if(!textOfMessage.equals("")) {

            //SENDING USER
            SingleMainScreenMessage singleMainMessage=
                    new SingleMainScreenMessage(mChatId,ServerValue.TIMESTAMP);

            mDatabase.child("users").child(myId).child("main_screen_messages")
                    .child(mIdOfChatPerson).setValue(singleMainMessage.toMap());
            //RECEIVING USER
            mDatabase.child("users").child(mIdOfChatPerson).child("main_screen_messages")
                    .child(myId).setValue(singleMainMessage.toMap());

            //MESSAGES
            SingleMessage singleMessage = new SingleMessage(
                    myId, textOfMessage, ServerValue.TIMESTAMP);
            LastMessage lastMessage = new LastMessage(
                    singleMessage.getUId(), singleMessage.getMessage(),
                    ServerValue.TIMESTAMP, false);

            mChatRef.child("messages").push().setValue(singleMessage.toMap());
            mChatRef.child("last_message").setValue(lastMessage.toMap());

            //NOTIFICATION
            sendNotification(mIdOfChatPerson,textOfMessage);


            //EDIT TEXT
            mMessageInput.setText("");
            hideKeyboard(ChatActivity.this);
        }
    }

    private void sendNotification(final String idOfChatPerson, final String textOfMessage) {
        DatabaseReference allTokens=mDatabase.child("tokens");


        allTokens.child(idOfChatPerson).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Token token= dataSnapshot.getValue(Token.class);
                if (token != null) {

                    Data data = new Data(myId, textOfMessage, mChatId, myName, idOfChatPerson, R.drawable.ic_notification);
                    Sender sender = new Sender(data, token.getToken());

                    mAPIService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        }
                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.d("TAG", "Failure while sending notification");
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void hideKeyboard(Activity activity){

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void changeReadStatus(){
        if(mChatId !=null) {
             mDatabase.child("chats").child(mChatId).child("last_message").addValueEventListener(mOnLastMessageListener);
        }
    }
    private void setLastMessageStatus(LastMessage lastMessage){

            boolean ifRead = lastMessage.isIfRead();
            String uId = lastMessage.getUId();
            ifMe = uId.equals(myId);

            if (ifMe) {
                if (ifRead) {
                    mSeenStatus.setText(R.string.chat_status_seen_text);
                } else {
                    mSeenStatus.setText(R.string.chat_status_send_text);
                }
                mSeenStatus.setVisibility(View.VISIBLE);

            } else {
                if (isOnTheBottom) {
                    mDatabase.child("chats").child(mChatId).child("last_message").child("ifRead").setValue(true);
                }
                mSeenStatus.setVisibility(View.GONE);
            }
    }
    public void showOptions(View view){
        Intent intent=new Intent(this,ChatOptionsActivity.class);
        intent.putExtra("chatId", mChatId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isOnTheBottom=false;

        //REMOVE ALL LISTENERS
        if(mDatabase!=null){
            mDatabase.child("users").child(mIdOfChatPerson)
                    .child("data").removeEventListener(mOnChatPersonDataListener);
        }
        if(mChatRef!=null) {
            mChatRef.child("data").removeEventListener(mOnDataChangeListener);
            mChatRef.child("last_message").removeEventListener(mOnLastMessageListener);
            mChatRef.child("messages").removeEventListener(mOnMessageListener);
            }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
