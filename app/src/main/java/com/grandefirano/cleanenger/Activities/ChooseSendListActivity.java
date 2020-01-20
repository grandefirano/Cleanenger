package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.Adapter.ChooseSendAdapter;

import java.util.ArrayList;

public class ChooseSendListActivity extends AppCompatActivity implements ChooseSendAdapter.OnItemListener {

    //FIREBASE
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;

    //ID OF CURRENT USER
    private String myId;

    //VIEW
    private CheckBox mSelectedAllCheckBox;
    private RecyclerView mRecyclerView;

    //RECYCLERVIEW IMPLEMENTS
    private ChooseSendAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //ARRAYS
    private ArrayList<String> mListOfFriends=new ArrayList<>();
    private boolean[] mListOfChecked;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_send_list);

        //VIEWS
        mRecyclerView=findViewById(R.id.selectToSendRecyclerView);
        mSelectedAllCheckBox=findViewById(R.id.selectAllCheckbox);

        //FIREBASE AND ID
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        myId=mAuth.getCurrentUser().getUid();

        //SETTING RECYCLERVIEW PROPERTIES
        mLayoutManager= new LinearLayoutManager(this);
        mAdapter=new ChooseSendAdapter(this,mListOfFriends,mListOfChecked,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);

        //DOWNLOAD LIST OF FRIENDS FROM DATABASE
        downloadFriends();

    }

    public void send(View view){

        ArrayList<String> listToSend=new ArrayList<>();

        //GET CHECKED USERS
        for(int i=0;i<mListOfFriends.size();i++){
            if(mListOfChecked[i]){
                listToSend.add(mListOfFriends.get(i));
            }
        }
        //RETURN LIST OF USERS
        if(listToSend.size()>0){
            Intent returnIntent=new Intent();
            returnIntent.putExtra("listToSend",listToSend);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }else{
            Toast.makeText(this,"Nobody is selected",Toast.LENGTH_SHORT).show();
        }

    }


    private void downloadFriends(){
        mDatabaseReference.child("users").child(myId)
                .child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              //DOWNLOAD AND UPDATE ALL FRIENDS SIMULTANEOUSLY

             for(DataSnapshot snap: dataSnapshot.getChildren()){
                 String id=snap.getKey();
                 mListOfFriends.add(id);
             }
             mListOfChecked=new boolean[mListOfFriends.size()];
             mAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    @Override
    public void onItemClick(int position, ImageView imageView) {

        if(mListOfChecked[position]==false) {
            //IF HADN'T BEEN CHECCKED
            imageView.setImageResource(R.drawable.ic_check_box_purple);
            mListOfChecked[position] = true;
        }
        else {
            //IF HAD BEEN CHECKED BEFORE
            mListOfChecked[position] = false;
            imageView.setImageResource(R.drawable.ic_check_box_outline_blank_grey);
        }
        //IF ALL SELECTED CHECK TOOLBAR BOX
        boolean ifAll=true;
        for(boolean b:mListOfChecked){ if(!b)ifAll=false; }
        mSelectedAllCheckBox.setChecked(ifAll);
        mAdapter.setSelectedAll(ifAll);

    }

    public void checkAll(View view){

        //WHEN CHECK ALL BUTTON PRESSED
        boolean b;
        if(mAdapter.getSelectedAll())
            b=false;
        else b=true;

        //SET ALL CHECKBOX
        for (int i = 0; i < mListOfChecked.length; i++) mListOfChecked[i] = b;

        mAdapter.setSelectedAll(b);
        mAdapter.notifyDataSetChanged();
    }

}
