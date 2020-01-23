package com.grandefirano.cleanenger.single_items;

public class SingleMessageFeedItem {

    private String mImageResource;
    private String mPersonText;
    private String mMessageText;
    private String mDate;
    private boolean mIfRead;

    public SingleMessageFeedItem(String imageResource, String person, String message, boolean ifRead,String date){
        mImageResource=imageResource;
        mPersonText=person;
        mMessageText=message;
        mIfRead=ifRead;
        mDate=date;

    }

    public String getImageResource() {
        return mImageResource;
    }

    public String getPersonText() {
        return mPersonText;
    }

    public String getMessageText() {
        return mMessageText;
    }

    public boolean isIfRead() {
        return mIfRead;
    }
}
