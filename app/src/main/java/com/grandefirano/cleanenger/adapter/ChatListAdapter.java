package com.grandefirano.cleanenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.Utilities;
import com.grandefirano.cleanenger.singleItems.SingleMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private String myId;
    private String mProfilePhotoUri;

    private ViewHolder clickedView;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private ArrayList<SingleMessage> mMessagesList;


    public ChatListAdapter(Context context,ArrayList<SingleMessage> messageList, String id, String profilePhoto){

        mContext=context;
        mProfilePhotoUri=profilePhoto;
        myId=id;
        mMessagesList=messageList;




    }
//    private ChildEventListener mListener= new ChildEventListener() {
//        @Override
//        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            if(!dataSnapshot.getKey().equals("last_message")){
//                SingleMessage message=dataSnapshot.getValue(SingleMessage.class);
//                mMessagesList.add(message);
//                notifyDataSetChanged();}
//            recyclerView.scrollToPosition(getItemCount()-1);
//        }
//        @Override
//        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//        @Override
//        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//        @Override
//        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) { }
//    };


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==MSG_TYPE_RIGHT){
            View view=LayoutInflater.from(mContext).inflate(R.layout.single_chat_row_right,parent,false);
            return new ChatListAdapter.ViewHolder(view);
        }else{
            View view=LayoutInflater.from(mContext).inflate(R.layout.single_chat_row_left,parent,false);
            return new ChatListAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SingleMessage singleMessage=mMessagesList.get(position);

        holder.bodyMessageTextView.setText(singleMessage.getMessage());

        Picasso.with(mContext)
                .load(mProfilePhotoUri)
                .into(holder.profilePhotoImageView);

        holder.dateMessageTextView.setText(Utilities.getProperDateFormat(singleMessage.getDate()));



    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        if(convertView==null){
//            LayoutInflater inflater=(LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView=inflater.inflate(R.layout.chat_msg_row,parent,false);
//            final ViewHolder holder = new ViewHolder();
//            holder.authorName= convertView.findViewById(R.id.author);
//            holder.body= convertView.findViewById(R.id.message);
//            holder.params=(LinearLayout.LayoutParams)holder.authorName.getLayoutParams();
//            convertView.setTag(holder);
//        }
//
//        final SingleMessage message= getItem(position);
//        final ViewHolder holder=(ViewHolder) convertView.getTag();
//
//        String msg=message.getMessage();
//        boolean isMe= message.getuId().equals(myId);
//
//        String author;
//        if(isMe){
//            author="Me";
//        }else {
//            author = musernameOfChatPerson;
//        }
//        holder.authorName.setText(author);
//        holder.body.setText(msg);
//
//        setChatRowAppearance(isMe,holder);
//
//        return convertView;
//
//    }

//    private void setChatRowAppearance(boolean isItMe,ViewHolder holder){
//
//        if(isItMe){
//            holder.params.gravity= Gravity.END;
//            holder.authorName.setTextColor(Color.GREEN);
//            holder.body.setBackgroundResource(R.drawable.singlechat2);
//        }else{
//            holder.params.gravity=Gravity.START;
//            holder.authorName.setTextColor(Color.BLUE);
//            holder.body.setBackgroundResource(R.drawable.singlechat1);
//        }
//
//        holder.authorName.setLayoutParams(holder.params);
//        holder.body.setLayoutParams(holder.params);
//
//    }

    @Override
    public int getItemViewType(int position) {
        if(myId.equals(mMessagesList.get(position).getuId())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }

    }

    //    static class ViewHolder{
//        TextView authorName;
//        TextView body;
//        LinearLayout.LayoutParams params;
//    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView bodyMessageTextView;
        ImageView profilePhotoImageView;
        TextView dateMessageTextView;


        public ViewHolder(@NonNull View itemView)  {
            super(itemView);
            bodyMessageTextView=itemView.findViewById(R.id.messagePersonTextView);
            profilePhotoImageView=itemView.findViewById(R.id.messagePersonImageView);
            dateMessageTextView=itemView.findViewById(R.id.chatMessageDateTextView);
            itemView.setOnClickListener(this);



        }


        @Override
        public void onClick(View v) {
            if(clickedView!=null){
                //clicked exist
                clickedView.dateMessageTextView.setVisibility(View.GONE);
                if(clickedView.getItemViewType()==MSG_TYPE_LEFT)
                    clickedView.bodyMessageTextView.setBackgroundResource(R.drawable.background_left_chat_row);
                else clickedView.bodyMessageTextView.setBackgroundResource(R.drawable.background_right_chat_row);

                if(!clickedView.equals(this)) {
                    dateMessageTextView.setVisibility(View.VISIBLE);
                    if(getItemViewType()==MSG_TYPE_LEFT)
                        bodyMessageTextView.setBackgroundResource(R.drawable.background_left_chat_row_clicked);
                    else bodyMessageTextView.setBackgroundResource(R.drawable.background_right_chat_row_clicked);
                    clickedView = this;
                }else{
                    clickedView=null;
                }

            }else{
                //clicked don't exist
                dateMessageTextView.setVisibility(View.VISIBLE);
                if(getItemViewType()==MSG_TYPE_LEFT)
                    bodyMessageTextView.setBackgroundResource(R.drawable.background_left_chat_row_clicked);
                else bodyMessageTextView.setBackgroundResource(R.drawable.background_right_chat_row_clicked);
                clickedView=this;
            }



        }
    }
    public void hideDate(int position){


    }

    public void updateProfilePhoto(String profilePhotoUri){
        mProfilePhotoUri=profilePhotoUri;
    }


}
