package com.grandefirano.cleanenger.Activities;

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
import com.grandefirano.cleanenger.Notifications.APIService;
import com.grandefirano.cleanenger.Notifications.Client;
import com.grandefirano.cleanenger.Notifications.Data;
import com.grandefirano.cleanenger.Notifications.MyResponse;
import com.grandefirano.cleanenger.Notifications.Sender;
import com.grandefirano.cleanenger.Notifications.Token;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.SingleItems.ChatData;
import com.grandefirano.cleanenger.SingleItems.LastMessage;
import com.grandefirano.cleanenger.SingleItems.UserData;
import com.grandefirano.cleanenger.Adapter.ChatListAdapter;
import com.grandefirano.cleanenger.SingleItems.SingleMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    String mIdOfChatPerson;

    String profilePhoto;

    EditText mMessageInput;
    String mchatId;
    String myId;
    boolean isOnTheBottom=false;

    String myName;

    private ChatListAdapter mAdapter;
    private DatabaseReference mDatabase;
    private DatabaseReference mChatRef;
    private RecyclerView mChatRecycleView;

    private CircleImageView mPersonImageView;
    private TextView mPersonNameTextView;
    private TextView mSeenStatus;

    APIService mAPIService;
    boolean notify=false;

    private ArrayList<SingleMessage> mMessagesList=new ArrayList<>();

    private ValueEventListener onLastMessageListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(isOnTheBottom) {
                    String uId=(String)dataSnapshot.child("uId").getValue();
                if (uId!=null && !uId.equals(myId)) {

                    mDatabase.child("chats").child(mchatId).child("last_message").child("ifRead").setValue(true);
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

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
    private ValueEventListener mOnLastMessageListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists())
            setLastMessageStatus(dataSnapshot);

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
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            if(dataSnapshot.getKey().equals("last_message")){
//                setLastMessageStatus(dataSnapshot);
//            }else if(dataSnapshot.getKey().equals("data")) {
////                int color=ChatListAdapter.DEFAULT_CHAT_COLOR;
////                if(dataSnapshot.child("color").exists())
////                    color=dataSnapshot.child("color").getValue(Integer.class);
////
////                int size=16;
////                if(dataSnapshot.child("textSize").exists())
////                    size=dataSnapshot.child("textSize").getValue(Integer.class);
////
////                mAdapter.setColor(color);
////                mAdapter.setTextSize(size);
//            }

        }
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

        isOnTheBottom=true;

        //INTENT
        Intent intent=getIntent();
        mchatId=intent.getStringExtra("chatId");
        mIdOfChatPerson=intent.getStringExtra("id");


        //FIREBASE
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user=auth.getCurrentUser();
        if(user==null){
            finish();
        }else {
            myId = user.getUid();
        }
        mDatabase= FirebaseDatabase.getInstance().getReference();



        SharedPreferences sharedPreferences=getSharedPreferences(MainActivity.SHARED_PREFS,Context.MODE_PRIVATE);
        //MY NAME
        myName=sharedPreferences.getString(MainActivity.MY_NAME,myId);


        if(mchatId!=null) {
            mChatRef=mDatabase.child("chats").child(mchatId);
            mChatRef.child("messages").addChildEventListener(mOnMessageListener);
            mChatRef.child("data").addValueEventListener(mOnDataChangeListener);
            mChatRef.child("last_message").addValueEventListener(mOnLastMessageListener);
        }


        //VIEW FINDING

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




        mDatabase.child("users").child(mIdOfChatPerson).child("data").addValueEventListener(new ValueEventListener() {
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


        //ADAPTER
        mAdapter= new ChatListAdapter(this, mMessagesList,
                myId,profilePhoto);

        mChatRecycleView.setAdapter(mAdapter);
        mChatRecycleView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        mChatRecycleView.setLayoutManager(linearLayoutManager);

        mAPIService= Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

        mChatRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //IF CANNOT SCROLL THEN IS ON THE BOTTOM
                isOnTheBottom= !recyclerView.canScrollVertically(1);
            }
        });
        //ADJUST RECYCLER VIEW TO THE KEYBOARD
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

        notify=true;

        final String textOfMessage=mMessageInput.getText().toString();
        if(!textOfMessage.equals("")) {

            //SENDING USER
            mDatabase.child("users").child(myId).child("main_screen_messages")
                    .child(mIdOfChatPerson).setValue(mchatId);
            //RECEIVING USER

            mDatabase.child("users").child(mIdOfChatPerson).child("main_screen_messages")
                    .child(myId).setValue(mchatId);


            //Message
            sendNotification(mIdOfChatPerson,textOfMessage);


            SingleMessage singleMessage = new SingleMessage(myId, textOfMessage, ServerValue.TIMESTAMP);
            LastMessage lastMessage = new LastMessage(singleMessage.getuId(), singleMessage.getMessage(), ServerValue.TIMESTAMP, false);


            mChatRef.child("messages").push().setValue(singleMessage.toMap());


            mChatRef.child("last_message").setValue(lastMessage.toMap());
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

                    Data data = new Data(myId, textOfMessage, mchatId, myName, idOfChatPerson, R.drawable.ic_notification);
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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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






    private void changeReadStatus(){
        if(mchatId!=null) {

             mDatabase.child("chats").child(mchatId).child("last_message").addValueEventListener(mOnLastMessageListener);
                Log.d("dddd","changReadStat");
        }
    }
    private void setLastMessageStatus(DataSnapshot dataSnapshot){

        LastMessage lastMessage=dataSnapshot.getValue(LastMessage.class);
        if(lastMessage!=null) {

            boolean ifRead = lastMessage.isifRead();
            String uId = lastMessage.getuId();
            boolean ifMe = uId.equals(myId);

            //TODO:





            if (ifMe) {
                if (ifRead) {
                    mSeenStatus.setText(R.string.chat_status_seen_text);
                } else {
                    mSeenStatus.setText(R.string.chat_status_send_text);
                }
                mSeenStatus.setVisibility(View.VISIBLE);

            } else {
                if (isOnTheBottom) {
                    mDatabase.child("chats").child(mchatId).child("last_message").child("ifRead").setValue(true);
                }
                mSeenStatus.setVisibility(View.GONE);
            }
        }
    }
    public void showOptions(View view){
        Intent intent=new Intent(this,ChatOptionsActivity.class);
        intent.putExtra("chatId",mchatId);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        isOnTheBottom=false;
        if(mDatabase!=null) {
            mDatabase.child("chats").child(mchatId).child("last_message").removeEventListener(onLastMessageListener);

        }
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
       // changeReadStatus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
