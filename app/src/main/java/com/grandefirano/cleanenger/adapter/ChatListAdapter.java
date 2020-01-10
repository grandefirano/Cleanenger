package com.grandefirano.cleanenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.singleItems.SingleMessage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ChatListAdapter extends BaseAdapter {

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String myId;
     String musernameOfChatPerson;
    private ArrayList<DataSnapshot> mSnapshotList;


    public ChatListAdapter(Activity activity, DatabaseReference ref, String id,String usernameOfChatPerson){

        mActivity=activity;
        musernameOfChatPerson=usernameOfChatPerson;
        myId=id;
        mDatabaseReference=ref;
        mDatabaseReference.addChildEventListener(mListener);

        mSnapshotList= new ArrayList<>();

    }
    private ChildEventListener mListener= new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            if(!dataSnapshot.getKey().equals("last_message")){
                mSnapshotList.add(dataSnapshot);
                notifyDataSetChanged();}
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

    static class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mSnapshotList.size();
    }

    @Override
    public SingleMessage getItem(int position) {
        DataSnapshot snapshot = mSnapshotList.get(position);

        return snapshot.getValue(SingleMessage.class);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.chat_msg_row,parent,false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName= convertView.findViewById(R.id.author);
            holder.body= convertView.findViewById(R.id.message);
            holder.params=(LinearLayout.LayoutParams)holder.authorName.getLayoutParams();
            convertView.setTag(holder);
        }

        final SingleMessage message= getItem(position);
        final ViewHolder holder=(ViewHolder) convertView.getTag();

        String msg=message.getMessage();
        boolean isMe= message.getuId().equals(myId);

        String author;
        if(isMe){
            author="Me";
        }else {
            author = musernameOfChatPerson;
        }
        holder.authorName.setText(author);
        holder.body.setText(msg);

        setChatRowAppearance(isMe,holder);

        return convertView;

    }

    private void setChatRowAppearance(boolean isItMe,ViewHolder holder){

        if(isItMe){
            holder.params.gravity= Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.singlechat2);
        }else{
            holder.params.gravity=Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.singlechat1);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);

    }


    public void cleanup(){
        mDatabaseReference.removeEventListener(mListener);
    }
}
