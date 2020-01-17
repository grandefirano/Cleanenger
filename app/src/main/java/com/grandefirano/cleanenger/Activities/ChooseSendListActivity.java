package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.adapter.ChooseSendAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static com.grandefirano.cleanenger.Activities.PhotoHelper.getFileExtension;

public class ChooseSendListActivity extends AppCompatActivity implements ChooseSendAdapter.OnItemListener {

    private ChooseSendAdapter mAdapter;
    private ArrayList<String> mListOfFriends=new ArrayList<>();
    private boolean[] mListOfChecked;
    private DatabaseReference mDatabaseReference;
    private RecyclerView mRecyclerView;
    private CheckBox mSelectedAllCheckBox;
    private FirebaseAuth mAuth;
    private String myId;
    private RecyclerView.LayoutManager mLayoutManager;
    private byte[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_send_list);

        Intent intent=getIntent();
        data=intent.getByteArrayExtra("mBytesOfDataArray");


        mRecyclerView=findViewById(R.id.selectToSendRecyclerView);
        mSelectedAllCheckBox=findViewById(R.id.selectAllCheckbox);

        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        myId=mAuth.getCurrentUser().getUid();

        mLayoutManager= new LinearLayoutManager(this);
        mAdapter=new ChooseSendAdapter(this,mListOfFriends,mListOfChecked,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setHasFixedSize(true);

        downloadFriends();

    }

    public void send(View view){
        ArrayList<String> listToSend=new ArrayList<>();
        for(int i=0;i<=mListOfFriends.size();i++){
            if(mListOfChecked[i]){
                listToSend.add(mListOfFriends.get(i));
            }
        }
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


             for(DataSnapshot snap: dataSnapshot.getChildren())   {
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
            imageView.setImageResource(R.drawable.ic_check_box_purple);
            mListOfChecked[position] = true;
        }
        else {
            mListOfChecked[position] = false;
            imageView.setImageResource(R.drawable.ic_check_box_outline_blank_grey);
        }
        boolean ifAll=true;
        for(boolean b:mListOfChecked){
            if(!b)ifAll=false;
            Log.d("ddddddLIST",String.valueOf(b));
        }
        mSelectedAllCheckBox.setChecked(ifAll);
        mAdapter.setSelectedAll(ifAll);

    }

    public void checkAll(View view){
        boolean b;
        //int selected;

        if(mAdapter.getSelectedAll()) {
            b=false;
            //selected=mAdapter.SELECTION_NOTHING;

        }else {
            b=true;
            //selected=mAdapter.SELECTION_ALL;
        }
        for (int i = 0; i < mListOfChecked.length; i++) {
            mListOfChecked[i] = b;
        }
        mAdapter.setSelectedAll(b);
        mAdapter.notifyDataSetChanged();
    }
    private void goToMain(){
        Intent intent=new Intent(this,MainActivity.class);
        finish();
        startActivity(intent);
    }
}
