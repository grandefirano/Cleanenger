package com.grandefirano.cleanenger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.Utilities;
import com.grandefirano.cleanenger.single_items.SingleMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private Context mContext;
    private String myId;
    private String mProfilePhotoUri;

    private ViewHolder clickedView;
    //private ViewHolder lastView;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private ArrayList<SingleMessage> mMessagesList;

    public static final int DEFAULT_CHAT_COLOR=100;
    public static final int RED_CHAT_COLOR=101;
    public static final int BLUE_CHAT_COLOR=102;
    public static final int GREEN_CHAT_COLOR=103;
    public static final int YELLOW_CHAT_COLOR=104;

    private int mChatColor;
    private int mTextSize;


    private static final int YELLOW_CHAT_BACKGROUND=R.drawable.background_left_chat_row_yellow;
    private static final int RED_CHAT_BACKGROUND=R.drawable.background_left_chat_row_red;
    private static final int GREEN_CHAT_BACKGROUND=R.drawable.background_left_chat_row_green;
    private static final int BLUE_CHAT_BACKGROUND=R.drawable.background_left_chat_row_blue;
    private static final int DEFAULT_CHAT_BACKGROUND=R.drawable.background_left_chat_row;

    private static final int YELLOW_CHAT_BACKGROUND_CLICKED=R.drawable.background_left_chat_row_yellow_clicked;
    private static final int RED_CHAT_BACKGROUND_CLICKED=R.drawable.background_left_chat_row_red_clicked;
    private static final int GREEN_CHAT_BACKGROUND_CLICKED=R.drawable.background_left_chat_row_green_clicked;
    private static final int BLUE_CHAT_BACKGROUND_CLICKED=R.drawable.background_left_chat_row_blue_clicked;
    private static final int DEFAULT_CHAT_BACKGROUND_CLICKED=R.drawable.background_left_chat_row_clicked;



    public void setColor(int color) {

        mChatColor=color;

    }
    public void setTextSize(int size){

            mTextSize = size;

    }


    public ChatListAdapter(Context context,ArrayList<SingleMessage> messageList, String id, String profilePhoto){

        mContext=context;
        mProfilePhotoUri=profilePhoto;
        myId=id;
        mMessagesList=messageList;

    }


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

        setSizeInView(holder);

        if(holder.getItemViewType()==MSG_TYPE_LEFT){
           setColorForLeftIfClicked(false,holder);
        }

        SingleMessage singleMessage=mMessagesList.get(position);

        holder.bodyMessageTextView.setText(singleMessage.getMessage());

        Picasso.with(mContext)
                .load(mProfilePhotoUri)
                .into(holder.profilePhotoImageView);

        holder.dateMessageTextView.setText(Utilities
                .getProperDateFormat(singleMessage.getDate(),true));

    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(myId.equals(mMessagesList.get(position).getUId())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView bodyMessageTextView;
        ImageView profilePhotoImageView;
        TextView dateMessageTextView;

        private ViewHolder(@NonNull View itemView)  {
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
                    setColorForLeftIfClicked(false,clickedView);

                else clickedView.bodyMessageTextView.setBackgroundResource(R.drawable.background_right_chat_row);

                if(!clickedView.equals(this)) {
                    dateMessageTextView.setVisibility(View.VISIBLE);
                    if(getItemViewType()==MSG_TYPE_LEFT)
                        setColorForLeftIfClicked(true,this);
                    else bodyMessageTextView.setBackgroundResource(R.drawable.background_right_chat_row_clicked);
                    clickedView = this;
                }else{
                    clickedView=null;
                }
            }else{
                //clicked don't exist
                dateMessageTextView.setVisibility(View.VISIBLE);
                if(getItemViewType()==MSG_TYPE_LEFT)
                    setColorForLeftIfClicked(true,this);
                else
                    bodyMessageTextView.setBackgroundResource
                            (R.drawable.background_right_chat_row_clicked);

                clickedView=this;
            }
        }
    }

    private void setColorForLeftIfClicked(boolean clicked,ViewHolder holder) {
        holder.bodyMessageTextView.setTextColor(ContextCompat.getColor(mContext,R.color.colorChatFontBright));
        if(clicked){

            switch (mChatColor) {
                case YELLOW_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(YELLOW_CHAT_BACKGROUND_CLICKED);
                    break;
                case RED_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(RED_CHAT_BACKGROUND_CLICKED);
                    break;
                case GREEN_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(GREEN_CHAT_BACKGROUND_CLICKED);
                    break;
                case BLUE_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(BLUE_CHAT_BACKGROUND_CLICKED);
                    break;
                default:
                    holder.bodyMessageTextView.setBackgroundResource(DEFAULT_CHAT_BACKGROUND_CLICKED);
                    //holder.bodyMessageTextView.setTextColor(ContextCompat.getColor(mContext,R.color.colorChatFont));

            }
        }else {
            switch (mChatColor) {
                case YELLOW_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(YELLOW_CHAT_BACKGROUND);
                    break;
                case RED_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(RED_CHAT_BACKGROUND);
                    break;
                case GREEN_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(GREEN_CHAT_BACKGROUND);
                    break;
                case BLUE_CHAT_COLOR:
                    holder.bodyMessageTextView.setBackgroundResource(BLUE_CHAT_BACKGROUND);
                    break;
                default:
                    holder.bodyMessageTextView.setBackgroundResource(DEFAULT_CHAT_BACKGROUND);
            }
        }
    }
    private void setSizeInView(ViewHolder holder){
        if(mTextSize==0){
            holder.bodyMessageTextView.setTextSize(16);
        }else {
            holder.bodyMessageTextView.setTextSize(mTextSize);
        }
    }

    public void updateProfilePhoto(String profilePhotoUri){
        mProfilePhotoUri=profilePhotoUri;
    }

}
