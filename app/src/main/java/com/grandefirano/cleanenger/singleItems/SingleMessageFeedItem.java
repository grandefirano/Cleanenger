package com.grandefirano.cleanenger.singleItems;

public class SingleMessageFeedItem {


    //TODO:Message Block zmienic na to
    //private String muId;
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
