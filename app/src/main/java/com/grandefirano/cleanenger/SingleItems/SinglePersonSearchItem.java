package com.grandefirano.cleanenger.SingleItems;

public class SinglePersonSearchItem {

    private String mPersonId;
    private String mPersonText;
    private String mImageResource;
    private boolean mIsFriend;

    public SinglePersonSearchItem( String personId,String person,String imageResource,boolean isFriend){

        mPersonId=personId;
        mPersonText=person;
        mImageResource=imageResource;
        mIsFriend=isFriend;


    }

    public String getImageResource() {
        return mImageResource;
    }

    public String getPersonText() {
        return mPersonText;
    }

    public String getPersonId() {
        return mPersonId;
    }

    public boolean isFriend() {
        return mIsFriend;
    }
}
